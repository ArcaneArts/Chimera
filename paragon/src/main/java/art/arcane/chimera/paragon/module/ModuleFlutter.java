package art.arcane.chimera.paragon.module;

import art.arcane.chimera.paragon.api.ParagonModule;
import art.arcane.chimera.paragon.api.Platforms;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.ChronoLatch;
import art.arcane.quill.format.Form;
import art.arcane.quill.io.IO;
import art.arcane.quill.tools.Download;
import lombok.Getter;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleFlutter extends ParagonModule {
    @Getter
    private String flutterVersion = "1.20.4";
    private String flutterWindowsDownload = "https://storage.googleapis.com/flutter_infra/releases/stable/windows/flutter_windows_%v-stable.zip";
    private String flutterMacDownload = "https://storage.googleapis.com/flutter_infra/releases/stable/macos/flutter_macos_%v-stable.zip";
    private String flutterChannel = "beta";

    public ModuleFlutter() {

    }

    @Override
    public void onEnable() {
        super.onEnable();
        flutterChannel(flutterChannel);
        flutterUpgrade();
        flutterPrecache();
        flutterDoctor();
    }

    @Override
    public boolean isInstalled() {
        return getModuleDirectory() != null && getModuleDirectory().exists() && getModuleDirectory().isDirectory() && getModuleDirectory().list().length > 0;
    }

    @Override
    public void onInstall() {
        String url = getDownloadURL();
        File download = new File(getTempDirectory(), IO.hash(url) + ".zip");

        if (!download.exists()) {
            downloadFlutter(url, download);
        }

        ChronoLatch cl = new ChronoLatch(1000);
        AtomicInteger v = new AtomicInteger(0);
        i("Installing Flutter SDK");
        ZipUtil.unpack(download, getModuleDirectory().getParentFile(), name ->
        {
            v.getAndIncrement();

            if (cl.flip()) {
                v("Still Installing Flutter: " + Form.f(v.get()) + " files installed");
            }

            return name;
        });
        i("Flutter SDK Installed");
    }

    public void downloadFlutter(String url, File download) {
        i("Downloading Flutter SDK");
        v("- SDK DL: " + url);
        v("- SDK ZIP: " + download.getPath());
        v("- SDK INSTALL: " + getModuleDirectory().getPath());
        try {
            Download.fromURL(url).to(download).monitorInterval(1000).monitor((m) -> v("Downloading Flutter SDK: " + Form.pc(m.getPercentComplete(), 0) + " @ " + Form.memSize(m.getLength() - m.getRemainingBytes(), 0) + " of " + Form.memSize(m.getLength(), 0) + " ~" + Form.duration(m.getEstimatedTimeRemaining(), 0) + " Left")).start().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUninstall() {
        IO.delete(getModuleDirectory());
    }

    public KList<File> getFlutterProjectFolders() {
        KList<File> m = new KList<>();

        for (File i : getGitModule().getFileRepo().listFiles()) {
            if (i.isDirectory()) {
                File pspec = new File(i, "pubspec.yaml");
                File nh = new File(i, "notflutter.signal");

                if (nh.exists()) {
                    continue;
                }

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

    public String getDownloadURL() {
        return (Platforms.isWindows() ? flutterWindowsDownload : flutterMacDownload).replaceAll("\\Q%v\\E", getFlutterVersion());
    }

    public void buildWeb(String project) {
        executePathCommand(new File(getGitModule().getFileRepo(), project), "flutter build web --release --no-tree-shake-icons");
    }

    public void buildAPK(String project) {
        executePathCommand(new File(getGitModule().getFileRepo(), project), "flutter build apk --no-shrink --release --no-tree-shake-icons -v");
    }

    public void buildAAB(String project) {
        executePathCommand(new File(getGitModule().getFileRepo(), project), "flutter build appbundle --release --no-shrink --no-tree-shake-icons -v");
    }

    public void flutterClean(String project) {
        executePathCommand(new File(getGitModule().getFileRepo(), project), "flutter clean -v");
    }

    public void flutterDoctor() {
        executePathCommand(getGitModule().getFileRepo(), "flutter doctor -v");
    }

    public void flutterPrecache() {
        executePathCommand(getGitModule().getFileRepo(), "flutter precache");
    }

    public void flutterChannel(String v) {
        executePathCommand(getGitModule().getFileRepo(), "flutter channel " + v);
    }

    public void flutterUpgrade() {
        executePathCommand(getGitModule().getFileRepo(), "flutter upgrade");
    }
}
