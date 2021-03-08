package art.arcane.chimera.paragon;

import art.arcane.chimera.core.microservice.ChimeraServiceWorker;
import art.arcane.chimera.core.microservice.ServiceWorker;
import art.arcane.chimera.core.protocol.generation.BigJob;
import art.arcane.chimera.core.protocol.generation.GatewayFunction;
import art.arcane.chimera.core.protocol.generation.Types;
import art.arcane.chimera.paragon.module.ModuleFlutter;
import art.arcane.chimera.paragon.module.ModuleGit;
import art.arcane.chimera.paragon.module.ModuleGradle;
import art.arcane.quill.collections.KList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProtoParagon extends ChimeraServiceWorker {
    @ServiceWorker
    private ModuleGit git = new ModuleGit();

    @ServiceWorker
    private ModuleFlutter flutter = new ModuleFlutter();

    @ServiceWorker
    private ModuleGradle gradle = new ModuleGradle();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Types(String.class)
    @GatewayFunction
    public KList<String> getGradleProjects() {
        return getGradle().getGradleProjects();
    }

    @Types(String.class)
    @GatewayFunction
    public KList<String> getFlutterProjects() {
        return getFlutter().getFlutterProjects();
    }

    @BigJob
    @GatewayFunction
    public Boolean gradleClean(String project) {
        getGradle().gradleClean(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean gradleBuild(String project) {
        getGradle().gradleClean(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean gradleArtifact(String project) {
        getGradle().gradleArtifact(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean flutterBuildAPK(String project) {
        getFlutter().buildAPK(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean flutterBuildAAB(String project) {
        getFlutter().buildAAB(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean flutterBuildWeb(String project) {
        getFlutter().buildWeb(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean flutterClean(String project) {
        getFlutter().flutterClean(project);
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean gitDiscard() {
        getGit().discardChanges();
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean gitPull() {
        getGit().pull();
        return true;
    }


    @BigJob
    @GatewayFunction
    public Boolean flutterUpgrade() {
        getFlutter().flutterUpgrade();
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean flutterPrecache() {
        getFlutter().flutterPrecache();
        return true;
    }

    @BigJob
    @GatewayFunction
    public Boolean gitSync() {
        getGit().sync();
        return true;
    }
}
