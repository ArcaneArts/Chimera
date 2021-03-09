package art.arcane.chimera.core.util;

import art.arcane.quill.Quill;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.io.IO;
import art.arcane.quill.io.StreamGobbler;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.CMD;
import art.arcane.quill.service.ConsoleServiceWorker;
import art.arcane.quill.service.QuillService;
import art.arcane.quill.service.ServiceWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ProjectConfigurator extends QuillService {
    private static final String mf = """
            Manifest-Version: 1.0
            Main-Class: art.arcane.chimera.$l.$uService
                        
            """;
    private static final String gradle = """
            dependencies {
                api project(":core")
            }
            """;
    private static final String protoSrc = """
            package art.arcane.chimera.$l;

            import art.arcane.chimera.core.microservice.ChimeraServiceWorker;
            import art.arcane.chimera.core.protocol.generation.GatewayFunction;
            import art.arcane.chimera.core.protocol.generation.ServiceFunction;

            public class Proto$u extends ChimeraServiceWorker {
                @ServiceFunction
                private int add(int a, int b)
                {
                    return a + b;
                }

                @GatewayFunction
                private int subtract(int a, int b)
                {
                    return a - b;
                }

                @Override
                public void onEnable() {
                   
                }

                @Override
                public void onDisable() {

                }
            }
            """;

    private static final String src = """     
            package art.arcane.chimera.$l;

            import art.arcane.chimera.core.Chimera;
            import art.arcane.chimera.core.microservice.ChimeraBackendService;
            import art.arcane.chimera.core.microservice.ServiceWorker;
            import art.arcane.chimera.core.protocol.EDN;
            import art.arcane.chimera.core.protocol.generation.Protocol;
            import art.arcane.quill.io.IO;
            import art.arcane.quill.logging.L;

            public class $uService extends ChimeraBackendService {

                public static void main(String[] a) {
                    Chimera.start(a);
                }

                @ServiceWorker
                @Protocol
                private Proto$u $l = new Proto$u();

                public $uService() {
                    super("$u");
                }

                @Override
                public void onEnable() {
                    getConsole().registerCommand("test-command", (a) ->
                    {
                        L.i("This is a test command!");

                        return true;
                    });
                }

                @Override
                public void onDisable() {

                }
            }        
            """;

    private String projectName = "Chimera";
    private String appName = "kraken";

    @ServiceWorker
    private ConsoleServiceWorker console = new ConsoleServiceWorker();

    public ProjectConfigurator() {
        super("ChimeraSetup");
    }

    public static void start() {
        Quill.start(new String[0]);
    }

    @Override
    public void onEnable() {
        console.registerCommand("newService", new CMD() {
            @Override
            public boolean onCommand(String... params) {
                String projectName = params[0];
                setupNewService(projectName);

                return true;
            }
        });
    }

    private void setupNewService(String projectName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("gradlew", "clean");
            Process p = pb.start();
            new StreamGobbler(p.getInputStream(), "").start();
            new StreamGobbler(p.getErrorStream(), "ERROR: ").start();
            p.waitFor();
        } catch (Throwable e) {

        }

        String u = projectName;
        String l = u.toLowerCase();
        File root = new File(l);

        if (root.exists()) {
            L.w(root.getAbsolutePath() + " Already exists!");
            return;
        }

        root.mkdirs();

        File main = new File(root, "src/main/java/art/arcane/chimera/" + l + "/" + u + "Service.java");
        File proto = new File(root, "src/main/java/art/arcane/chimera/" + l + "/Proto" + u + ".java");
        main.getParentFile().mkdirs();
        new File(root, "src/main/resources").mkdirs();
        new File(root, "src/test/resources").mkdirs();
        new File(root, "src/test/java").mkdirs();
        File mf = new File(root, "META-INF/MANIFEST.MF");
        mf.getParentFile().mkdirs();
        File bg = new File(root, "build.gradle");

        try {
            IO.writeAll(main, src.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + main.getAbsolutePath());
            IO.writeAll(proto, protoSrc.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + proto.getAbsolutePath());
            IO.writeAll(mf, ProjectConfigurator.mf.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + mf.getAbsolutePath());
            IO.writeAll(bg, gradle);
            L.i("Created " + bg.getAbsolutePath());
            KList<String> lines = KList.from(IO.readLines(new FileInputStream("settings.gradle")));
            lines.add(1, "include '" + l + "'");
            IO.writeAll(new File("settings.gradle"), lines.toString("\n"));
            L.i("Editing " + new File("settings.gradle").getAbsolutePath());
            IO.writeAll(new File("build.gradle"), KList.from(IO.readLines(new FileInputStream("build.gradle"))).convert((i) -> {
                if (i.contains("boolean allowProtogen = true;")) {
                    return i.replaceAll("\\Qboolean allowProtogen = true;\\E", "boolean allowProtogen = false;");
                }

                return i;
            }).toString("\n"));
            L.i("Editing " + new File("build.gradle").getAbsolutePath());
            L.i("Building Projects...");
            L.flush();
            ProcessBuilder pb = new ProcessBuilder("gradlew", "build");
            Process p = pb.start();
            J.attempt(p::waitFor);
            new StreamGobbler(p.getInputStream(), "").start();
            new StreamGobbler(p.getErrorStream(), "ERROR: ").start();
            L.i("Running Protogen");
            L.flush();
            pb = new ProcessBuilder("gradlew", "protogen");
            Process p1 = pb.start();
            J.attempt(p1::waitFor);
            new StreamGobbler(p1.getInputStream(), "").start();
            new StreamGobbler(p1.getErrorStream(), "ERROR: ").start();
            L.i("Running ProtogenSRC");
            L.flush();
            pb = new ProcessBuilder("gradlew", "protogenSrc");
            Process p2 = pb.start();
            J.attempt(p2::waitFor);
            new StreamGobbler(p2.getInputStream(), "").start();
            new StreamGobbler(p2.getErrorStream(), "ERROR: ").start();

            IO.writeAll(new File("build.gradle"), KList.from(IO.readLines(new FileInputStream("build.gradle"))).convert((i) -> {
                if (i.contains("boolean allowProtogen = true;")) {
                    return i.replaceAll("\\Qboolean allowProtogen = false;\\E", "boolean allowProtogen = true;");
                }

                return i;
            }).toString("\n"));
            L.i("Editing " + new File("build.gradle").getAbsolutePath());
            L.i("Rebuilding Projects...");
            L.flush();
            pb = new ProcessBuilder("gradlew", "build");
            Process fp = pb.start();
            J.attempt(fp::waitFor);
            new StreamGobbler(fp.getInputStream(), "").start();
            new StreamGobbler(fp.getErrorStream(), "ERROR: ").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
