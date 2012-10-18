package org.intellij.scalastyle.settings

import com.intellij.openapi.options.Configurable
import javax.swing.{JPanel, JComponent}

/**
 * @author Alefas
 * @since 18.10.12
 */
class ScalastyleSettingsConfigurable extends Configurable {
  def createComponent(): JComponent = new JPanel() //todo: Implement UI here

  def isModified: Boolean = false //todo: true if Apply button should be enabled

  def apply() {
    //todo: apply settings change
  }

  def reset() {
    //todo: reset settings
  }

  def disposeUIResources() {}

  def getDisplayName: String = "Scalastyle"

  def getHelpTopic: String = null
}
