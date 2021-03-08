package art.arcane.chimera.paragon.api;

import art.arcane.chimera.core.microservice.ChimeraServiceWorker;
import art.arcane.chimera.paragon.ProtoParagon;
import art.arcane.chimera.paragon.module.ModuleGit;
import lombok.Getter;

import java.io.File;

public abstract class ParagonModule extends ChimeraServiceWorker implements Module {

    @Getter
    private String name;

    @Getter
    private final File rootDirectory;

    public ParagonModule() {
        this(new File("paragon-work"), "Unknown");
        name = getClass().getSimpleName().replaceAll("\\QModule\\E", "");
    }

    public ParagonModule(File rootDirectory, String name) {
        this.name = name;
        this.rootDirectory = rootDirectory;
    }

    public ModuleGit getGitModule() {
        return ((ProtoParagon) getParent()).getGit();
    }

    @Override
    public void onEnable() {
        install();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract boolean isInstalled();

    @Override
    public void install() {
        if (isInstalled()) {
            return;
        }

        i("Installing Module " + getName());
        onInstall();
        i("Installed Module " + getName());
    }

    @Override
    public void uninstall() {
        if (!isInstalled()) {
            return;
        }

        i("Uninstalling Module " + getName());
        onUninstall();
        i("Uninstalled Module " + getName());
    }

    public abstract void onInstall();

    public abstract void onUninstall();
}
