package org.intellij.scalastyle.inspection

import com.intellij.codeInspection._
import com.intellij.psi.{PsiDocumentManager, PsiFile}
import org.jetbrains.plugins.scala.lang.psi.api.ScalaFile
import org.scalastyle._
import collection.mutable.ArrayBuffer
import org.scalastyle.EndFile
import org.scalastyle.StyleException
import org.scalastyle.StyleError
import org.scalastyle.EndWork
import org.scalastyle.StartFile
import org.scalastyle.StartWork
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

/**
 * @author Alefas
 * @since 17.10.12
 */
class ScalastyleBasedInspection extends LocalInspectionTool {
  override def checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    val messageHelper = new MessageHelper(getClass().getClassLoader())

    file match {
      case scalaFile: ScalaFile if scalaFile.getVirtualFile.getExtension == "scala" =>

        val document: Document = PsiDocumentManager.getInstance(scalaFile.getProject).getDocument(scalaFile)
        if (document == null) return ProblemDescriptor.EMPTY_ARRAY

        //todo: change it to some appropriate configuration
        val configuration: ScalastyleConfiguration = ScalastyleConfiguration.getDefaultConfiguration()

        try {
          val messages = Checker.verifySource[FileSpec](configuration,
            configuration.checks.filter(_.enabled), new FileSpec {
              def name: String = scalaFile.getName
            }, scalaFile.getText)
          val problems: ArrayBuffer[ProblemDescriptor] = new ArrayBuffer[ProblemDescriptor]()
          messages foreach {
            case StartWork()          =>
            case EndWork()            =>
            case StartFile(file)      =>
            case EndFile(file)        =>
            case error: StyleError[_] =>
              val lineNumber = error.lineNumber.map(_ - 1).getOrElse(0)

              if (lineNumber >= 0 && lineNumber < document.getLineCount) {
                val problemHighlightingType = error.level match {
                  case WarningLevel => ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                  case ErrorLevel   => ProblemHighlightType.GENERIC_ERROR
                  case _            => ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                }

                val startOffset: Int = document.getLineStartOffset(lineNumber)
                val endOffset: Int = document.getLineEndOffset(lineNumber)

                val message = error.customMessage match {
                  case Some(message) => message
                  case None          => messageHelper.message(getClass.getClassLoader, error.key, error.args)
                }

                //todo: probably scalaFile as dependent item is not really good idea, but this seems
                //      the best variant due to problem of different parser trees.
                problems += manager.createProblemDescriptor(
                  scalaFile, new TextRange(startOffset, endOffset), message, problemHighlightingType
                )
              }
            case StyleException(file, clazz, message, stacktrace, line, column) => //todo: cope with StyleException
          }
          problems.toArray
        }
        catch {
          case e: Exception => ProblemDescriptor.EMPTY_ARRAY
            //todo: why parser informs about illegal start of simple pattern as exception?
            //      so this is just to ignore
            //      actual problem is the following: while we haven't correctly parsed program, we haven't style checking
        }
      case _ => ProblemDescriptor.EMPTY_ARRAY
    }
  }
}