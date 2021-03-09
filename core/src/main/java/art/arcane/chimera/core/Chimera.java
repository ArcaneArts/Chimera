package art.arcane.chimera.core;

import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.chimera.core.protocol.generation.*;
import art.arcane.chimera.core.util.ProjectConfigurator;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.execution.J;
import art.arcane.quill.io.IO;
import art.arcane.quill.json.JSONArray;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public class Chimera {
    public static Class<? extends ChimeraService> delegateClass = null;
    public static ChimeraService delegate = null;

    public static final void fix(Class<?> derp) {
        // "Fixed"
    }

    public static void start(String[] a) {
        for (String v : a) {
            if (v.equals("-config")) {
                setupChimeraProject(a);

                return;
            }

            if (v.equals("-protogen")) {
                for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
                    try {
                        Class<? extends ChimeraService> s = (Class<? extends ChimeraService>) Class.forName(i.getClassName());
                        if (ChimeraService.class.isAssignableFrom(s)) {
                            startProtogen(s, a);
                            return;
                        }
                    } catch (Throwable e) {
                        L.ex(e);
                    }
                }

                return;
            }

            if (v.equals("-codegen")) {
                for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
                    try {
                        Class<? extends ChimeraService> s = (Class<? extends ChimeraService>) Class.forName(i.getClassName());
                        if (ChimeraService.class.isAssignableFrom(s)) {
                            startCodegen(s, a);
                            return;
                        }
                    } catch (Throwable e) {
                        L.ex(e);
                    }
                }

                return;
            }
        }

        for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
            try {
                Class<? extends ChimeraService> s = (Class<? extends ChimeraService>) Class.forName(i.getClassName());
                if (ChimeraService.class.isAssignableFrom(s)) {
                    start(s, a);
                    return;
                }
            } catch (Throwable e) {

            }
        }
    }

    private static void setupChimeraProject(String[] a) {
        ProjectConfigurator.start();
        L.flush();
        System.exit(0);
    }

    private static void startCodegen(Class<? extends ChimeraService> s, String[] a) {
        delegateClass = s;

        KMap<String, File> dff = new KMap<String, File>();
        String[] flutterProjects = new String[0];
        String[] flutterClientProjects = new String[0];
        File out = new File("protogen");
        File root = new File("protogen").getParentFile();
        File outSrc = new File("out.java");
        String pkg = "";
        String name = "";

        for (String i : a) {

            if (i.startsWith("-root=")) {
                String sp = i.split("\\Qroot=\\E")[1];
                root = new File(sp);
            }
            if (i.startsWith("-flutter=")) {
                String sp = i.split("\\Qflutter=\\E")[1];
                flutterProjects = sp.contains(",") ? sp.split("\\Q,\\E") : new String[]{sp};
                L.i("Preparing Codegen for " + Arrays.toString(flutterProjects));
            }
            if (i.startsWith("-flutterscan=")) {
                String sp = i.split("\\Qflutterscan=\\E")[1];
                flutterClientProjects = sp.contains(",") ? sp.split("\\Q,\\E") : new String[]{sp};
                L.i("Preparing Java Codegen from Flutter for " + Arrays.toString(flutterClientProjects));

                for (String c : flutterClientProjects) {
                    String pname = c.split("\\Q@\\E")[0];
                    String fname = c.split("\\Q@\\E")[1];
                    File lib = new File(root, pname + "/" + fname);
                    L.i("Queued Client Function scan for " + pname + " at " + lib.getPath());
                    dff.put(pname, lib);
                }
            }
            if (i.startsWith("-protofolder=")) {
                String sp = i.split("\\Qprotofolder=\\E")[1];
                out = new File(sp);
            }

            if (i.startsWith("-out=")) {
                String sp = i.split("\\Qout=\\E")[1];
                outSrc = new File(sp);
            }

            if (i.startsWith("-pkg=")) {
                pkg = i.split("\\Qpkg=\\E")[1];
            }

            if (i.startsWith("-name=")) {
                name = i.split("\\Qname=\\E")[1];
            }
        }

        out.mkdirs();

        KList<ProtoFunction> all = new KList<>();
        KList<ProtoFunction> gateway = new KList<>();

        for (File i : out.listFiles()) {
            try {
                JSONArray ja = new JSONArray(IO.readAll(i));
                for (int j = 0; j < ja.length(); j++) {
                    all.add(new Gson().fromJson(ja.getJSONObject(j).toString(0), ProtoFunction.class));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        KList<File> flutterProjectFiles = new KList<>();

        for (String i : flutterProjects) {
            flutterProjectFiles.add(new File(root, i + "/lib/chimera/protocol.dart"));
        }

        for (ProtoFunction i : all) {
            if (i.getType().equals(EDX.TYPE_GATEWAY)) {
                gateway.add(i);
            }

            if (!i.getResultType().equals(i.getFixedResult())) {
                ProtoExport.warnings.add("The type " + i.getResultType() + " is not directly supported! We are using " + i.getFixedResult() + ", BOXING! (RETURN TYPE at " + i.getName() + "(...))");
            }

            for (ProtoParam j : i.getParams()) {
                if (!j.getRealType().equals(j.getFixedType())) {
                    ProtoExport.warnings.add("The param type " + j.getRealType() + " is not directly supported! We are using " + j.getFixedType() + ", BOXING! (PAR AT " + i.getName() + "(..., " + j.getName() + ", ...))");
                }
            }
        }
        KMap<String, String> result = new KMap<>();
        try {
            result = new ProtoExport(all, dff).exportJava(pkg, name);
            IO.writeAll(outSrc, result.get("src"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        L.i("Generated Service Class with " + all.size() + " Functions");

        for (File i : flutterProjectFiles) {
            new ProtoExport(gateway, dff).exportDart(i, i.getParentFile().getParentFile().getParentFile().getName());
            L.i("Generated Network Class with " + gateway.size() + " functions for Flutter Project: " + i.getParentFile().getParentFile().getParentFile().getName());
        }

        for (String i : dff.k()) {
            new ProtoExport(gateway, dff).exportDartClient(i, dff.get(i), result.get("dart." + i), result.get("dname." + i));
            L.i("Generated " + i + " chimera initializers");
        }

        L.flush();

        L.i("====================================================================================================");
        L.i("Generated with " + ProtoExport.warnings.size() + " Warning(s)");
        L.i("====================================================================================================");

        L.flush();
        for (String i : ProtoExport.warnings) {
            L.w(i);
        }

        L.flush();
        if (ProtoExport.warnings.size() > 0) {
            L.i("====================================================================================================");

        }
        L.flush();
        System.exit(0);
    }

    private static void startProtogen(Class<? extends ChimeraService> s, String[] a) {
        delegateClass = s;
        KList<ProtoFunction> localFunctions = new KList<>();

        for (Field i : s.getDeclaredFields()) {
            if (i.isAnnotationPresent(Protocol.class)) {
                localFunctions.addAll(ProtoBuilder.functions(i.getType(), null));
            }
        }

        JSONArray ja = new JSONArray();

        for (ProtoFunction i : localFunctions) {
            ja.put(new JSONObject(new Gson().toJson(i)));
        }

        File out = new File("protogen.json");

        for (String i : a) {
            if (i.startsWith("-protoout=")) {
                String sp = i.split("\\Qprotoout=\\E")[1];
                out = new File(sp);
            }
        }

        out.getParentFile().mkdirs();
        try {
            IO.writeAll(out, ja.toString(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        L.i("Wrote " + localFunctions.size() + " Functions to " + out.getAbsolutePath());
        L.flush();
        System.exit(0);
    }

    public static void start(Class<? extends ChimeraService> service, String[] a) {
        if (delegate != null) {
            crashStack("Service attempted to start when an existing delegate was already running!");
            return;
        }

        try {
            delegateClass = service;
            delegate = ChimeraService.initializeConfigured(service);
            assert delegate != null;
        } catch (Throwable e) {
            L.ex(e);
            crash("Failed to initialize Chimera Service Delegate Class");
            return;
        }

        try {
            delegate.startService();
        } catch (Throwable e) {
            L.ex(e);
            crash("Failed to start Chimera Service Delegate onEnable");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Chimera::shutdown));
    }

    public static void shutdown() {
        try {
            delegate.stopService();
        } catch (Throwable e) {

        }

        delegateClass = null;
        delegate = null;
        System.exit(0);
    }

    public static void crash() {
        crash("¯\\_(ツ)_/¯");
    }

    public static void crashStack(String message) {
        J.printStack(message);
        crash(message);
    }

    public static void crash(String message) {
        L.f("Chimera Service Crash: " + message);
        L.flush();
        System.exit(1);
    }

    public static String getDelegateModuleName() {
        return delegateClass.getCanonicalName().split("\\Q.\\E")[3];
    }
}
