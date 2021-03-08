package art.arcane.chimera.paragon.module;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import art.arcane.chimera.paragon.api.ParagonModule;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class ModuleGradle extends ParagonModule {

    public ModuleGradle() {

    }

    @Override
    public boolean isInstalled() {
        return true;
    }

    @Override
    public void onInstall() {

    }

    @Override
    public void onUninstall() {

    }

    public void gradleArtifact(String project) {
        executeConsoleCommand(getGitModule().getFileRepo(), "gradlew " + project + ":artifact");
    }

    public void gradleClean(String project) {
        executeConsoleCommand(getGitModule().getFileRepo(), "gradlew " + project + ":clean");
    }

    public void gradleBuild(String project) {
        executeConsoleCommand(getGitModule().getFileRepo(), "gradlew " + project + ":build");
    }

    public KList<String> getGradleProjects() {
        KList<String> m = new KList<>();

        try {
            for (String i : IO.readAll(new File(getGitModule().getFileRepo(), "settings.gradle")).split("\\Q\n\\E")) {
                if (i.startsWith("include '")) {
                    m.add(i.split("\\Q'\\E")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return m;
    }
}
