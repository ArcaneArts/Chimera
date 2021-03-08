package art.arcane.chimera.paragon;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraServiceWorker;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.format.Form;
import art.arcane.quill.io.IO;
import art.arcane.quill.io.StreamSucker;
import art.arcane.quill.logging.L;
import art.arcane.quill.tools.Download;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@EqualsAndHashCode(callSuper = true)
@Data
public class RepositoryManager extends ChimeraServiceWorker {
    private String flutterVersion = "1.20.4";
    private String flutterWindowsDownload = "https://storage.googleapis.com/flutter_infra/releases/stable/windows/flutter_windows_%v-stable.zip";
    private String flutterMacDownload = "https://storage.googleapis.com/flutter_infra/releases/stable/macos/flutter_macos_%v-stable.zip";
    private String username = "cyberpwnn";
    private String accessToken = "someaccesstoken";
    private String remote = "https://github.com/MPMTeam/chimera.git";
    private String flutterChannel = "beta";
    private transient Repository repository;
    private transient Git git;
    private transient String remoteUrl;
    private transient File fileWork;
    private transient File fileFlutter;
    private transient File fileRepo;
    private transient CredentialsProvider credentials;
    private transient volatile boolean ready = false;
    private transient String lastLine = "Idle";

    @Override
    public void onEnable() {
        J.a(this::sync);
    }

    public void sync() {
        ready = false;
        try {
            //@builder
            setCredentials(new UsernamePasswordCredentialsProvider(getUsername(), getAccessToken()));
            setFileWork(new File(System.getenv("user.home") + "/Paragon/work"));
            getFileWork().mkdirs();
            IO.delete(new File(getFileWork(), "cache"));
            setFileRepo(new File(getFileWork(), "chimera"));
            setFileFlutter(new File(getFileWork(), "flutter"));
            installFlutter();

            if (getFileRepo().exists() && getFileRepo().listFiles().length > 0) {
                setGit(new Git(new FileRepositoryBuilder()
                        .setGitDir(new File(getFileRepo(), ".git"))
                        .build()));
            } else {
                L.i("Cloning to " + getFileRepo());
                setGit(Git.cloneRepository()
                        .setCloneAllBranches(true)
                        .setURI(getRemote())
                        .setCredentialsProvider(getCredentials())
                        .setDirectory(getFileRepo()).call());
            }

            discardChanges();
            pull();
            flutterSetup(getFlutterChannel());
            getFlutterProjects().forEach(this::flutterClean);
            gradleClean();
            gradle();
            gradleProtogen();
            gradleBuild();

            ready = true;
            lastLine = "Ready";
            //@done
        } catch (Throwable e) {
            L.ex(e);
            Chimera.crash("Failed to pull reset or idk bro. Figure it out.");
        }
    }

    public boolean isWindows() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("win") >= 0;
    }

    public boolean isMac() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("mac") >= 0 || System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("darwin") >= 0;
    }

    public void installFlutter() {
        if (!getFileFlutter().exists() || !getFileFlutter().isDirectory()) {
            try {
                String url = (isWindows() ? flutterWindowsDownload : flutterMacDownload).replaceAll("\\Q%v\\E", getFlutterVersion());
                File download = new File(getFileWork(), "cache/flutter." + url.split("\\Q.\\E")[url.split("\\Q.\\E").length - 1]);
                download.getParentFile().mkdirs();
                L.i("Downloading Flutter SDK from " + url + " to " + download.getAbsolutePath());
                Download.fromURL(url).to(download).monitorInterval(3000).monitor((m) ->
                {
                    L.i("Downloading Flutter SDK: " + Form.pc(m.getPercentComplete(), 0));
                }).start().get();
                L.i("Downloaded Flutter SDK. Installing...");
                ZipUtil.unpack(download, getFileFlutter().getParentFile(), name ->
                {
                    L.i("[Installing Flutter]: " + name);
                    return name;
                });
                L.i("Flutter SDK Installed");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void flutterWeb(String project) {
        executePathCommand(new File(getFileRepo(), project), "flutter build web --release --no-tree-shake-icons");
    }

    public void flutterAPK(String project) {
        executePathCommand(new File(getFileRepo(), project), "flutter build apk --no-shrink --release --no-tree-shake-icons -v");
    }

    public void flutterAAB(String project) {
        executePathCommand(new File(getFileRepo(), project), "flutter build appbundle --release --no-shrink --no-tree-shake-icons -v");
    }


    public void gradleArtifact(String project) {
        executeConsoleCommand(getFileRepo(), "gradlew " + project + ":artifact");
    }

    public void gradleClean() {
        executeConsoleCommand(getFileRepo(), "gradlew clean");
    }

    public void gradle() {
        executeConsoleCommand(getFileRepo(), "gradlew");
    }

    public void gradleArtifact() {
        executeConsoleCommand(getFileRepo(), "gradlew artifact");
    }

    public void gradleBuild() {
        executeConsoleCommand(getFileRepo(), "gradlew build");
    }

    public void flutterClean(String project) {
        executePathCommand(new File(getFileRepo(), project), "flutter clean -v");
    }

    public void flutterDoctor() {
        executePathCommand(getFileRepo(), "flutter doctor -v");
    }

    public void flutterPrecache() {
        executePathCommand(getFileRepo(), "flutter precache");
    }

    public void flutterSetup(String channel) {
        flutterChannel(channel);
        flutterUpgrade();
        flutterDoctor();
        flutterPrecache();
    }

    public void flutterChannel(String v) {
        executePathCommand(getFileRepo(), "flutter channel " + v);
    }

    public void flutterUpgrade() {
        executePathCommand(getFileRepo(), "flutter upgrade");
    }

    public void gradleClean(String project) {
        executeConsoleCommand(getFileRepo(), "gradlew " + project + ":clean");
    }

    public void discardChanges() {
        call(getGit().reset().setMode(ResetCommand.ResetType.HARD));
        L.i("Discarded Git Changes");
    }

    public void gradleProtogen() {
        executeConsoleCommand(getFileRepo(), "gradlew protogen");
        executeConsoleCommand(getFileRepo(), "gradlew protogenSrc");
    }

    private String prefix(String v) {
        if (isMac()) {
            return getFileRepo().getAbsolutePath() + File.separator + v;
        }

        return "cmd /c call " + getFileRepo().getAbsolutePath() + File.separator + v;
    }

    private String prefixPath(String v) {
        if (isMac()) {
            return v;
        }

        return "cmd /c call " + v;
    }

    public void executeConsoleCommand(File in, String fullCommand) {
        try {
            L.i("Running " + fullCommand + " in " + in.getAbsolutePath());
            //@builder
            ProcessBuilder pb = new ProcessBuilder(prefix(fullCommand).split("\\Q \\E"))
                    .directory(in)
                    .redirectErrorStream(true);
            Map<String, String> env = pb.environment();
            updateEnvironment(env);
            Process p = pb.start();
            //@done
            new StreamSucker(p.getInputStream(), (i) ->
            {
                L.i("Process: " + i);
                lastLine = i;
            });

            p.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void executePathCommand(File in, String fullCommand) {
        try {
            L.i("Running " + fullCommand + " in " + in.getAbsolutePath());
            //@builder
            ProcessBuilder pb = new ProcessBuilder(prefixPath(fullCommand).split("\\Q \\E"))
                    .directory(in)
                    .redirectErrorStream(true);
            Map<String, String> env = pb.environment();
            updateEnvironment(env);
            Process p = pb.start();
            //@done
            new StreamSucker(p.getInputStream(), (i) -> L.i("Process: " + i));

            p.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateEnvironment(Map<String, String> env) {
        if (isMac()) {
            String path = env.get("PATH");
            KList<String> pp = new KList<>();

            if (path.contains(":")) {
                pp = new KList<String>(path.split("\\Q:\\E"));
            } else {
                pp.add(path);
            }

            for (String i : pp.copy()) {
                if (i.contains("flutter/bin")) {
                    pp.remove(i);
                }
            }

            pp.add(new File(getFileFlutter(), "bin").getAbsolutePath());
            env.put("PATH", pp.toString(":"));
        }

        if (isWindows()) {
            String path = env.get("Path");
            KList<String> pp = new KList<>();

            if (path.contains(";")) {
                pp = new KList<String>(path.split("\\Q;\\E"));
            } else {
                pp.add(path);
            }

            for (String i : pp.copy()) {
                if (i.contains("flutter/bin")) {
                    pp.remove(i);
                }
            }

            pp.add(new File(getFileFlutter(), "bin").getAbsolutePath());
            env.put("Path", pp.toString(";"));
        }
    }

    public KList<String> getGradleProjects() {
        KList<String> m = new KList<>();

        try {
            for (String i : IO.readAll(new File(getFileRepo(), "settings.gradle")).split("\\Q\n\\E")) {
                if (i.startsWith("include '")) {
                    m.add(i.split("\\Q'\\E")[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return m;
    }

    public KList<File> getFlutterProjectFolders() {
        KList<File> m = new KList<>();

        for (File i : getFileRepo().listFiles()) {
            if (i.isDirectory()) {
                File pspec = new File(i, "pubspec.yml");

                if (pspec.exists()) {
                    m.add(i);
                }
            }
        }

        return m;
    }

    public KList<String> getFlutterProjects() {
        KList<String> m = new KList<>();

        for (File i : getFlutterProjectFolders()) {
            m.add(i.getName());
        }

        return m;
    }

    public String getName() {
        return remote;
    }

    public void push() {
        PushResult r = call(getGit().push());
        L.i("Push " + getName() + " Successfully");
    }

    public void commit(String message) {
        call(getGit().add().addFilepattern("."));
        call(getGit().commit().setMessage(message));
    }

    public void pull() {
        PullResult r = call(getGit().pull());
        if (r.isSuccessful()) {
            L.i("Pulled " + getName() + " Successfully");
        } else {
            L.f("Failed to pull");
        }
    }

    private byte[] getResourceBytes(String r) throws Throwable {
        InputStream in = getClass().getResourceAsStream("/" + r);
        byte[] d = IO.toByteArray(in);
        in.close();
        return d;
    }

    @Override
    public void onDisable() {

    }

    private <T> T call(GitCommand<?> c) {
        if (c instanceof TransportCommand) {
            TransportCommand<?, T> t = (TransportCommand<?, T>) c;
            t.setCredentialsProvider(getCredentials());
        }

        try {
            return (T) c.call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return null;
    }
}
