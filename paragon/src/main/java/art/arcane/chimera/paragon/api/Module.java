package art.arcane.chimera.paragon.api;

import art.arcane.quill.collections.KList;
import art.arcane.quill.io.StreamSucker;
import art.arcane.quill.logging.L;

import java.io.File;
import java.util.Map;

public interface Module {
    public String getName();

    public boolean isInstalled();

    public File getRootDirectory();

    public default File getModuleDirectory() {
        File f = new File(getRootDirectory(), getName());
        f.mkdirs();
        return f;
    }

    public default File getTempDirectory() {
        File f = new File(getRootDirectory(), "temp/" + getName());
        f.mkdirs();
        return f;
    }

    public default void reinstall() {
        uninstall();
        install();
    }

    public default void updateEnvironment(Map<String, String> env) {
        if (Platforms.isMac()) {
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

            pp.add(new File(getModuleDirectory(), "bin").getAbsolutePath());
            env.put("PATH", pp.toString(":"));
        }

        if (Platforms.isWindows()) {
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

            pp.add(new File(getModuleDirectory(), "bin").getAbsolutePath());
            env.put("Path", pp.toString(";"));
        }
    }

    public default String prefix(File pf, String v) {
        if (Platforms.isMac()) {
            return pf.getAbsolutePath() + File.separator + v;
        }

        return "cmd /c call " + pf.getAbsolutePath() + File.separator + v;
    }

    public default String prefixPath(String v) {
        if (Platforms.isMac()) {
            return v;
        }

        return "cmd /c call " + v;
    }

    public default void executeConsoleCommand(File in, String fullCommand) {
        try {
            L.i("Running " + fullCommand + " in " + in.getAbsolutePath());
            //@builder
            ProcessBuilder pb = new ProcessBuilder(prefix(in, fullCommand).split("\\Q \\E"))
                    .directory(in)
                    .redirectErrorStream(true);
            Map<String, String> env = pb.environment();
            updateEnvironment(env);
            Process p = pb.start();
            //@done
            new StreamSucker(p.getInputStream(), (i) ->
            {
                i("Process: " + i);
            });

            p.waitFor();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public default void executePathCommand(File in, String fullCommand) {
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

    public void install();

    public void uninstall();

    default void i(String message) {
        L.i("<" + getName() + ">: " + message);
    }

    default void f(String message) {
        L.f("<" + getName() + ">: " + message);
    }

    default void w(String message) {
        L.w("<" + getName() + ">: " + message);
    }

    default void v(String message) {
        L.v("<" + getName() + ">: " + message);
    }
}
