/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

package art.arcane.chimera.core.util;

import art.arcane.quill.Quill;
import art.arcane.quill.collections.KList;
import art.arcane.quill.io.IO;
import art.arcane.quill.io.StreamGobbler;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;
import art.arcane.quill.service.Service;
import art.arcane.quill.service.services.ConsoleService;

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
                        
            import art.arcane.chimera.core.protocol.generation.ServiceFunction;
            import art.arcane.quill.service.QuillService;
            import lombok.Data;
            import lombok.EqualsAndHashCode;
                        
            @EqualsAndHashCode(callSuper = true)
            @Data
            public class Proto$u extends QuillService {
                @Override
                public void onEnable() {
                        
                }
                        
                @Override
                public void onDisable() {
                        
                }
            }
            """;

    private static final String src = """     
            package art.arcane.chimera.mail;
                        
            import art.arcane.chimera.core.Chimera;
            import art.arcane.chimera.core.microservice.ChimeraService;
            import art.arcane.chimera.core.protocol.generation.Protocol;
            import art.arcane.quill.service.Service;
            import lombok.Data;
            import lombok.EqualsAndHashCode;
                        
            @EqualsAndHashCode(callSuper = true)
            @Data
            public class $uService extends ChimeraService {
                public static void main(String[] a) {
                    Chimera.start(a);
                }
                        
                @Service
                @Protocol
                private Proto$u mail = new Proto$u();
                        
                @Override
                public void onEnable() {
                    super.onEnable();
                }
                        
                @Override
                public void onDisable() {
                    super.onDisable();
                }
            }     
            """;

    private String projectName = "Chimera";
    private String appName = "kraken";

    @Service
    private ConsoleService console = new ConsoleService();

    public static void startConfigurator() {
        Quill.start(ProjectConfigurator.class, new String[0]);
    }

    @Override
    public void onEnable() {
        Quill.delegate = this;
        Quill.delegateClass = getClass();
        console.registerCommand("newService", params -> {
            String projectName = params[0];
            setupNewService(projectName);
            return true;
        });

        for (int i = 0; i < 128; i++) {
            System.out.println();
        }

        L.i("==================================================================================");
        L.i("Chimera Configurator");
        L.i("-----------------------------------------");
        L.i("newService <ServiceName>     Create new Java Services, upper cammel case names.");
        L.i("==================================================================================");
    }

    private void gradleCommand(String command, File path) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "call", path.getAbsolutePath() + "\\gradlew.bat", command);
            Process p = pb.start();
            new StreamGobbler(p.getInputStream(), "");
            new StreamGobbler(p.getErrorStream(), "ERROR: ");
            p.waitFor();
        } catch (Throwable e) {
            L.ex(e);
        }
    }

    private void setupNewService(String projectName) {
        try {
            String u = projectName;
            String l = u.toLowerCase();
            File rootProject = new File(new File(new File("derp").getAbsolutePath()).getParentFile().getParentFile().getAbsolutePath());
            File root = new File(rootProject, l);

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

            IO.writeAll(main, src.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + main.getPath());
            IO.writeAll(proto, protoSrc.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + proto.getPath());
            IO.writeAll(mf, ProjectConfigurator.mf.replaceAll("\\Q$u\\E", u).replaceAll("\\Q$l\\E", l));
            L.i("Created " + mf.getPath());
            IO.writeAll(bg, gradle);
            L.i("Created " + bg.getPath());
            KList<String> lines = KList.from(IO.readLines(new FileInputStream(new File(rootProject, "settings.gradle"))));
            lines.add(1, "include '" + l + "'");
            L.i("Editing " + new File(rootProject, "settings.gradle").getPath());
            IO.writeAll(new File(rootProject, "settings.gradle"), lines.toString("\n"));
            IO.writeAll(new File(rootProject, "build.gradle"), KList.from(IO.readLines(new FileInputStream(new File(rootProject, "build.gradle")))).convert((i) -> {
                if (i.contains("boolean allowProtogen = true;")) {
                    return i.replaceAll("\\Qboolean allowProtogen = true;\\E", "boolean allowProtogen = false;");
                }

                return i;
            }).toString("\n"));
            L.i("Editing " + new File(rootProject, "build.gradle").getPath());
            L.i("Building Projects...");
            L.flush();
            gradleCommand("build", rootProject);
            L.i("Running Protogen");
            L.flush();
            IO.writeAll(new File(rootProject, "build.gradle"), KList.from(IO.readLines(new FileInputStream(new File(rootProject, "build.gradle")))).convert((i) -> {
                if (i.contains("boolean allowProtogen = false;")) {
                    return i.replaceAll("\\Qboolean allowProtogen = false;\\E", "boolean allowProtogen = true;");
                }

                return i;
            }).toString("\n"));
            L.i("Editing " + new File(rootProject, "build.gradle").getPath());
            gradleCommand("protogen", rootProject);
            L.i("Running ProtogenSRC");
            L.flush();
            gradleCommand("protogenSrc", rootProject);
            L.i("Rebuilding Projects...");
            L.flush();
            gradleCommand("build", rootProject);
            L.i("=========================================");
            L.i("    Created Service " + u);
            L.i("-----------------------------------------");
            L.i("In Intellij Sync your gradle project!");
            L.i("=========================================");
            L.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
