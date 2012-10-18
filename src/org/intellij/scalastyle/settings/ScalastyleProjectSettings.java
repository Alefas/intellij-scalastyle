package org.intellij.scalastyle.settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Alefas
 * @since 18.10.12
 */
@State(
    name = "ScalastyleProjectSettings",
    storages = {
        @Storage(file = "$WORKSPACE_FILE$"),
        @Storage(file = "$PROJECT_CONFIG_DIR$/scalastyle_settings.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class ScalastyleProjectSettings implements PersistentStateComponent<ScalastyleProjectSettings>, ExportableComponent {
  //todo: add here fields to store scalastyle settings

  public static ScalastyleProjectSettings getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, ScalastyleProjectSettings.class);
  }

  public ScalastyleProjectSettings getState() {
    return this;
  }

  public void loadState(ScalastyleProjectSettings scalastyleProjectSettings) {
    XmlSerializerUtil.copyBean(scalastyleProjectSettings, this);
  }

  @NotNull
  public File[] getExportFiles() {
    return new File[]{PathManager.getOptionsFile("scalastyle_project_settings")};
  }

  @NotNull
  public String getPresentableName() {
    return "Scalastyle Project Settings";
  }
}
