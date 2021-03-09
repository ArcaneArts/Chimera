package art.arcane.chimera.core.protocol.generation;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.object.ServiceJob;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.collections.KSet;
import art.arcane.quill.format.Form;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.reaction.O;
import art.arcane.quill.tools.JarScanner;
import art.arcane.quill.tools.JarTools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Date;

public class ProtoExport {
    private KMap<String, Class<?>> realDobjects = new KMap<>();
    private KMap<String, String> dobject = new KMap<>();
    private KList<ProtoFunction> functions;
    private KMap<String, File> dartFunctionFiles;
    private KList<String> javaFromDartFunctions = new KList<>();
    public static KSet<String> warnings = new KSet<>();

    public ProtoExport(KList<ProtoFunction> functions, KMap<String, File> dartFunctionFiles) {
        L.DEDUPLICATE_LOGS = false;
        L.ACTIVE_FLUSH_INTERVAL = 50;
        L.IDLE_FLUSH_INTERVAL = 100;
        this.functions = functions;
        this.dartFunctionFiles = dartFunctionFiles;

        JarScanner js = new JarScanner(JarTools.getJar(getClass()), "");
        L.v("Scanning Jar: " + JarTools.getJar(getClass()).getAbsolutePath());
        try {
            js.scan();
        } catch (IOException e) {
            e.printStackTrace();
        }

        L.i("Reading " + Form.f(js.getClasses().size()) + " Classes for Dart Objects to convert...");

        for (Class<?> o : js.getClasses()) {
            if (o.isAnnotationPresent(Dart.class)) {
                registerDartObject(o);
            }
        }
    }

    public void exportDart(File f, String projectName) {
        KMap<String, KList<String>> classMap = new KMap<>();

        for (ProtoFunction i : functions) {
            if (!classMap.containsKey(i.getService())) {
                classMap.put(i.getService(), new KList<>());
            }

            classMap.get(i.getService()).add(exportDartFunction(i));
        }

        StringBuilder cb = new StringBuilder();
        String nl = "\n";
        String nl2 = nl + nl;

        cb.append("import 'dart:convert';");
        cb.append("import 'package:" + projectName + "/chimera/chimera.dart';" + nl);

        for (String protoService : classMap.keySet()) {
            cb.append("/// Represents the Remote " + Form.capitalize(protoService) + " Service on the Chimera Network" + nl);
            cb.append("class Chimera" + Form.capitalize(protoService) + nl);
            cb.append("{" + nl);
            for (String i : classMap.get(protoService)) {
                cb.append(i + nl);
            }
            cb.append("}" + nl);
        }

        for (String i : dobject.values()) {
            cb.append(i + nl);
        }

        cb.append("/// This is used for internal object discovery. DO NOT USE THIS." + nl);
        cb.append("class INTERNALChimeraObjectDiscovery {" + nl);
        cb.append("static dynamic doubleBlindInstantiate(String t, dynamic j){" + nl);
        L.i("Generating Dart object discovery lists. (AOT Casting)");

        for (String i : dobject.keySet()) {
            cb.append("if(t == '" + i + "'){" + nl);
            cb.append("return " + i + ".fromJson(j);" + nl);
            cb.append("}" + nl);
        }

        cb.append("print('ERROR: UNKNOWN TYPE: $t');" + nl);
        cb.append("return null;" + nl);
        cb.append("}" + nl);

        cb.append("/// Gets the type name of the object." + nl);
        cb.append("static String getTypeName(dynamic t){");
        for (String i : dobject.keySet()) {
            cb.append("if(t is " + i + "){" + nl);
            cb.append("return '" + i + "';");
            cb.append("}");
        }

        cb.append("print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');").append(nl);
        cb.append("return 'ERRORUnknownType';").append(nl);
        cb.append("}");

        cb.append("/// Unwraps the string into a real object." + nl);
        cb.append("static dynamic fromIdentifiedString(String t)");
        cb.append("=> WrappedObject.of(jsonDecode(t)).get();");

        cb.append("/// Checks if the given object is suppored for json & networking with chimera." + nl);
        cb.append("static bool isSupported(dynamic t)");
        cb.append("=> ");

        StringBuilder sb = new StringBuilder();

        for (String i : dobject.keySet()) {
            sb.append("|| t is " + i);
        }

        cb.append(sb.length() > 0 ? sb.substring(3) : "").append(";").append(nl);


        cb.append("/// Checks if the given TYPE is suppored for json & networking with chimera." + nl);
        cb.append("static bool isSupportedType(dynamic t)");
        cb.append("=> ");

        sb = new StringBuilder();

        for (String i : dobject.keySet()) {
            sb.append("|| t == " + i);
        }

        cb.append(sb.length() > 0 ? sb.substring(3) : "").append(";").append(nl);


        cb.append("/// Converts this object into a wrapped object the same way the chimera does it." + nl);
        cb.append("/// Great for storing in caches or files. Read with WhateverTypeYouUsed t = fromIdentifiedString(thisOutput)" + nl);
        cb.append("static String toIdentifiedString(dynamic t){");
        for (String i : dobject.keySet()) {
            cb.append("if(t is " + i + "){" + nl);
            cb.append("return jsonEncode(WrappedObject.create('" + i + "', t.toJson()).toWrappedJson());");
            cb.append("}");
        }

        cb.append("print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');" + nl);
        cb.append("return null;" + nl);
        cb.append("}");

        cb.append("}" + nl);

        try {
            IO.writeAll(f, cb.toString());
            L.i("Generated " + f.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPrimitiveDartType(ProtoType type) {
        return type.equals(ProtoType.STRING) || type.equals(ProtoType.INT) || type.equals(ProtoType.LONG) || type.equals(ProtoType.DOUBLE) || type.equals(ProtoType.BOOLEAN) || type.equals(ProtoType.VOID);
    }

    private String dartType(ProtoType type, String typeName, Class<?> lz, String dartType, String dartType2) {
        if (typeName.endsWith(".UUID")) {
            throw new RuntimeException("STOP USING UUID. Use ID instead!");
        }

        if (typeName.equals(Object.class.getCanonicalName())) {
            return "dynamic";
        }

        if (type.equals(ProtoType.VOID)) {
            return "void";
        }

        if (type.equals(ProtoType.BOOLEAN)) {
            return "bool";
        }

        if (type.equals(ProtoType.DOUBLE)) {
            return "double";
        }

        if (type.equals(ProtoType.INT) || type.equals(ProtoType.LONG)) {
            return "int";
        }

        if (type.equals(ProtoType.STRING)) {
            return "String";
        }

        if (type.equals(ProtoType.JSON_LIST)) {
            if (dartType != null) {
                return "List<" + dartType + ">";
            }

            return "List<dynamic>";
        }

        if (type.equals(ProtoType.JSON_MAP)) {
            if (dartType != null) {
                if (dartType2 != null) {
                    return "Map<" + dartType + "," + dartType2 + ">";
                }
                return "Map<" + dartType + ",dynamic>";
            }

            return "Map<dynamic,dynamic>";
        }

        map2DartObject(typeName, lz);
        return typeName.contains(".") ? new KList<String>(typeName.split("\\Q.\\E")).popLast() : typeName;
    }

    public static String dartTypeStatic(ProtoType type, String typeName, Class<?> lz, String dartType, String dartType2) {
        if (typeName.endsWith(".UUID")) {
            throw new RuntimeException("STOP USING UUID. Use ID instead!");
        }

        if (typeName.equals(Object.class.getCanonicalName())) {
            return "dynamic";
        }

        if (type.equals(ProtoType.VOID)) {
            return "void";
        }

        if (type.equals(ProtoType.BOOLEAN)) {
            return "bool";
        }

        if (type.equals(ProtoType.DOUBLE)) {
            return "double";
        }

        if (type.equals(ProtoType.INT) || type.equals(ProtoType.LONG)) {
            return "int";
        }

        if (type.equals(ProtoType.STRING)) {
            return "String";
        }

        if (type.equals(ProtoType.JSON_LIST)) {
            if (dartType != null) {
                return "List<" + dartType + ">";
            }

            return "List<dynamic>";
        }

        if (type.equals(ProtoType.JSON_MAP)) {
            if (dartType != null) {
                if (dartType2 != null) {
                    return "Map<" + dartType + "," + dartType2 + ">";
                }
                return "Map<" + dartType + ",dynamic>";
            }

            return "Map<dynamic,dynamic>";
        }

        return typeName.contains(".") ? new KList<String>(typeName.split("\\Q.\\E")).popLast() : typeName;
    }

    public void registerDartObject(Class<?> c) {
        map2DartObject(c.getCanonicalName(), c);
    }

    public void exportDartClient(String i, File file, String s, String cname) {
        File target = new File(file.getParentFile(), file.getName().split("\\Q.\\E")[0] + ".g.dart");
        File targeth = new File(file.getParentFile(), "chimera.dart");
        try {
            IO.writeAll(target, s);
            L.i("Generated " + i + " function map " + file.getName() + " -> " + target.getName());
            StringBuilder hh = new StringBuilder();
            String attempt = file.getAbsolutePath().toString().split("\\Q" + File.separator + "lib" + File.separator + "\\E")[1];
            attempt = attempt.replaceAll("\\Q" + File.separator + "\\E", "/");
            attempt = attempt.replaceAll("\\Q.dart\\E", ".g.dart");
            attempt = "import 'package:" + i + "/" + attempt + "';";
            hh.append("import 'package:hawkeye/hawkeye.dart';\n");
            hh.append("import 'package:hawkeye/chimera/global/functions.g.dart' as globals;\n");
            hh.append(attempt + "\n");
            hh.append("class Chimera{\n");
            hh.append("static Future<bool> initialize() async { bool v = await DefaultInvoker.initialize(INTERNALChimeraClientFunctionInvoker()");
            hh.append(",globals.INTERNALChimeraClientFunctionInvoker()");
            hh.append("); \n return v;\n");
            hh.append("}}\n");
            IO.writeAll(targeth, hh.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String listTypeOf(AnnotatedElement f, int index) {
        Types t = f.getDeclaredAnnotation(Types.class);

        if (t != null) {
            Class<?>[] l = t.value();

            if (l.length > index) {
                return dartTypeStatic(ProtoType.of(l[index]), l[index].getCanonicalName(), l[index], null, null);
            }
        }

        return null;
    }

    public static String listTypeOfJava(AnnotatedElement f, int index, String at) {
        Types t = f.getDeclaredAnnotation(Types.class);

        if (t != null) {
            Class<?>[] l = t.value();

            if (l.length > index) {
                return l[index].getCanonicalName();
            }
        } else if ((f instanceof Method && (((Method) f).getReturnType().equals(KList.class) || ((Method) f).getReturnType().equals(KMap.class))) || (f instanceof Field && (((Field) f).getType().equals(KList.class) || ((Field) f).getType().equals(KMap.class)))) {
            ProtoExport.warnings.add("The " + f.getClass().getSimpleName() + " " + ((Member) f).getName() + " uses generic types but is not annotated with @Typed(theType) in the class " + at);
        }

        return null;
    }

    private void map2DartObject(String type, Class<?> clz) {
        if (type.equals(String.class.getCanonicalName())) {
            return;
        }

        String typeName = type.contains(".") ? new KList<String>(type.split("\\Q.\\E")).popLast() : type;

        if (dobject.containsKey(typeName)) {
            return;
        }

        try {
            L.v("* Mapping Java Class " + type + " to Dart Object");
            Class<?> c = Class.forName(type);
            KList<Field> fields = new KList<>();

            for (Field i : c.getDeclaredFields()) {
                i.setAccessible(true);

                if (Modifier.isStatic(i.getModifiers()) || Modifier.isTransient(i.getModifiers())) {
                    continue;
                }

                fields.add(i);
            }

            StringBuilder cb = new StringBuilder();
            String nl = "\n";

            cb.append("/// " + typeName + " is a ghost-copy of the Java Object (" + type + ") on Chimera" + nl);
            cb.append("class " + typeName + "{" + nl);

            for (Field i : fields) {
                Types t = i.getDeclaredAnnotation(Types.class);
                cb.append(dartType(ProtoType.of(i.getType()), i.getType().getCanonicalName(), i.getType(), listTypeOf(i, 0), listTypeOf(i, 1)));
                cb.append(" " + i.getName() + ";" + nl);
            }

            cb.append("/// Converts JSON to " + typeName + nl);
            cb.append("static " + typeName + " fromJson(Map<String, dynamic> json){" + nl);

            for (Field i : fields) {
                ProtoType t = ProtoType.of(i.getType());
                if (t.equals(ProtoType.JSON_LIST)) {
                    String dt = listTypeOf(i, 0);
                    String cx = listTypeOfJava(i, 0, clz.getCanonicalName());
                    boolean jsobj = cx != null &&
                            !cx.equals(Object.class.getCanonicalName()) &&
                            ProtoType.of(Class.forName(cx)).equals(ProtoType.JSON_OBJECT);
                    dt = dt == null ? "dynamic" : dt;
                    cb.append("List<" + dt + "> l" + i.getName() + " = List<" + dt + ">();\n");
                    cb.append("(json['" + i.getName() + "'] as List<dynamic>).forEach((x)=>");
                    cb.append("l" + i.getName() + ".add(");
                    if (jsobj) {
                        cb.append(dt + ".fromJson(x as Map<String, dynamic>)");
                    } else {
                        cb.append("x as " + dt);
                    }

                    cb.append("));\n");
                }

                if (t.equals(ProtoType.JSON_MAP)) {
                    String dt = listTypeOf(i, 0);
                    String dt2 = listTypeOf(i, 1);
                    String cx = listTypeOfJava(i, 0, clz.getCanonicalName());
                    String cx2 = listTypeOfJava(i, 1, clz.getCanonicalName());
                    boolean jsobj = cx != null && !cx.equals(Object.class.getCanonicalName()) && ProtoType.of(Class.forName(cx)).equals(ProtoType.JSON_OBJECT);
                    boolean jsob2 = cx2 != null && !cx2.equals(Object.class.getCanonicalName()) && ProtoType.of(Class.forName(cx2)).equals(ProtoType.JSON_OBJECT);
                    dt = dt == null ? "dynamic" : dt;
                    dt2 = dt2 == null ? "dynamic" : dt2;
                    cb.append("Map<" + dt + "," + dt2 + "> m" + i.getName() + " = Map<" + dt + "," + dt2 + ">();\n");
                    cb.append("(json['" + i.getName() + "'] as Map<String, dynamic>).forEach((k,v)=>");
                    cb.append("m" + i.getName() + "[k] = ");
                    if (jsob2) {
                        cb.append(dt2 + ".fromJson(v as Map<String, dynamic>)");
                    } else {
                        cb.append("v as " + dt2);
                    }

                    cb.append(");\n");
                }
            }

            cb.append("return " + typeName + "()");
            for (Field i : fields) {
                ProtoType t = ProtoType.of(i.getType());

                if (t.equals(ProtoType.JSON_LIST)) {
                    cb.append(".." + i.getName() + " = l" + i.getName() + nl);
                    continue;
                }

                if (t.equals(ProtoType.JSON_MAP)) {
                    cb.append(".." + i.getName() + " = m" + i.getName() + nl);
                    continue;
                }

                cb.append(".." + i.getName() + " = " + computeAccessorValue("json['" + i.getName() + "']", i.getType()) + nl);
            }
            cb.append(";" + nl);
            cb.append("}" + nl);

            cb.append("/// Converts " + typeName + " to JSON " + nl);
            cb.append("Map<String, dynamic> toJson(){" + nl);
            cb.append("Map<String, dynamic> json = Map<String, dynamic>();" + nl);

            for (Field i : fields) {
                ProtoType t = ProtoType.of(i.getType());
                if (t.equals(ProtoType.JSON_LIST)) {
                    String dt = listTypeOf(i, 0);
                    String cx = listTypeOfJava(i, 0, clz.getCanonicalName());
                    boolean jsobj = cx != null && !cx.equals(Object.class.getCanonicalName()) && ProtoType.of(Class.forName(cx)).equals(ProtoType.JSON_OBJECT);
                    dt = dt == null ? "dynamic" : dt;
                    cb.append("List<dynamic> l" + i.getName() + " = List<dynamic>();\n");
                    cb.append(i.getName() + ".forEach((x)=>");
                    cb.append("l" + i.getName() + ".add(");
                    if (jsobj) {
                        cb.append("x.toJson()");
                    } else {
                        cb.append("x");
                    }

                    cb.append("));\n");
                }

                if (t.equals(ProtoType.JSON_MAP)) {
                    String dt = listTypeOf(i, 0);
                    String cx = listTypeOfJava(i, 0, clz.getCanonicalName());
                    boolean jsobj = cx != null && !cx.equals(Object.class.getCanonicalName()) && ProtoType.of(Class.forName(cx)).equals(ProtoType.JSON_OBJECT);
                    dt = dt == null ? "dynamic" : dt;
                    String dt2 = listTypeOf(i, 1);
                    String cx2 = listTypeOfJava(i, 1, clz.getCanonicalName());
                    boolean jsobj2 = cx2 != null && !cx2.equals(Object.class.getCanonicalName()) && ProtoType.of(Class.forName(cx2)).equals(ProtoType.JSON_OBJECT);
                    dt2 = dt2 == null ? "dynamic" : dt2;
                    cb.append("Map<String, dynamic> m" + i.getName() + " = Map<String, dynamic>();\n");
                    cb.append(i.getName() + ".forEach((k,v)=>");
                    cb.append("m" + i.getName() + "[k] = ");
                    if (jsobj2) {
                        cb.append("v.toJson()");
                    } else {
                        cb.append("v");
                    }

                    cb.append(");\n");
                }
            }

            for (Field i : fields) {
                cb.append("if(" + i.getName() + " != null){" + nl);
                ProtoType t = ProtoType.of(i.getType());
                if (t.equals(ProtoType.JSON_LIST)) {
                    cb.append("json['" + i.getName() + "'] = l" + i.getName() + ";" + nl);
                } else if (t.equals(ProtoType.JSON_MAP)) {
                    cb.append("json['" + i.getName() + "'] = m" + i.getName() + ";" + nl);
                } else {
                    cb.append("json['" + i.getName() + "'] = " + computeMapperValue(i.getName(), i.getType()) + ";" + nl);
                }
                cb.append("}" + nl);
            }

            cb.append("return json;" + nl);
            cb.append("}" + nl);

            if (typeName.equals("ID")) {
                cb.append("/// Generates a new Spec ID" + nl);
                cb.append("static ID random() => ID()..i=RNG.ss(64);" + nl);

                cb.append("/// Creates an ID from string. This is not checked for validity!" + nl);
                cb.append("static ID from(String v) => ID()..i=v;" + nl);

                cb.append("@override" + nl);
                cb.append("String toString() => i;" + nl);
            } else {
                cb.append("@override" + nl);
                cb.append("String toString() => jsonEncode(toJson());" + nl);
            }

            cb.append("}");

            realDobjects.put(typeName, clz);
            dobject.put(typeName, cb.toString());
        } catch (Throwable e) {
            L.f("Can't convert " + type + " to dart class!");
            L.ex(e);
        }
    }

    private String computeAccessorValue(String s, Class<?> type) {
        if (!isPrimitiveDartType(ProtoType.of(type))) {
            return type.getSimpleName() + ".fromJson((" + s + " ?? Map<String,dynamic>())" + " as Map<String, dynamic>)";
        }

        return s;
    }

    private String computeMapperValue(String s, Class<?> type) {
        if (!isPrimitiveDartType(ProtoType.of(type))) {
            return s + ".toJson()";
        }

        return s;
    }

    private String exportDartFunction(ProtoFunction i) {

        try {
            StringBuilder cb = new StringBuilder();
            String nl = "\n";

            String returnType = dartType(i.getResult(), i.getFixedResult(), Class.forName(i.getFixedResult()),
                    i.getT1() == null ? null : dartType(ProtoType.of(Class.forName(i.getT1())), i.getT1(), Class.forName(i.getT1()), null, null),
                    i.getT2() == null ? null : dartType(ProtoType.of(Class.forName(i.getT2())), i.getT2(), Class.forName(i.getT2()), null, null)
            );
            cb.append("/// Invokes " + i.getName() + "(...) on the Remote " + Form.capitalize(i.getService()) + " Service on the Chimera Network" + nl);
            cb.append("static Future<" + returnType + "> " + i.getName());
            cb.append("(");
            StringBuilder cc = new StringBuilder();
            for (ProtoParam j : i.getParams()) {
                cc.append(",");

                cc.append(dartType(j.getType(), j.getFixedType(), Class.forName(i.getFixedResult()),
                        j.getT1() == null ? null : dartType(ProtoType.of(Class.forName(j.getT1())), j.getT1(), Class.forName(j.getT1()), null, null),
                        j.getT2() == null ? null : dartType(ProtoType.of(Class.forName(j.getT2())), j.getT2(), Class.forName(j.getT2()), null, null)
                ) + " " + j.getName());
            }

            cb.append(cc.length() > 0 ? cc.substring(1) : cc.toString());

            cb.append(") async { try{ \n");

            for (ProtoParam j : i.getParams()) {
                if (j.getType().equals(ProtoType.JSON_LIST)) {

                    String dtype = "dynamic";
                    ProtoType t = ProtoType.VOID;

                    if (j.getT1() != null) {
                        Class<?> lt = Class.forName(j.getT1());
                        dtype = dartType(t = ProtoType.of(lt), j.getT1(), lt, null, null);
                    }

                    String xt = "List<" + dtype + ">";
                    cb.append("List<dynamic> l" + j.getName() + " = List<dynamic>();\n");

                    if (t.equals(ProtoType.JSON_OBJECT)) {
                        cb.append(j.getName() + ".forEach((x)=> l" + j.getName() + ".add(x.toJson()));\n");
                    } else {
                        cb.append(j.getName() + ".forEach((x)=> l" + j.getName() + ".add(x));\n");
                    }
                }

                if (j.getType().equals(ProtoType.JSON_MAP)) {

                    String dtype = "dynamic";
                    String dtype2 = "dynamic";
                    ProtoType t = ProtoType.VOID;
                    ProtoType t2 = ProtoType.VOID;

                    if (j.getT1() != null) {
                        Class<?> lt = Class.forName(j.getT1());
                        dtype = dartType(t = ProtoType.of(lt), j.getT1(), lt, null, null);
                    }

                    if (j.getT2() != null) {
                        Class<?> lt = Class.forName(j.getT2());
                        dtype2 = dartType(t2 = ProtoType.of(lt), j.getT2(), lt, null, null);
                    }

                    String xt = "Map<" + dtype + "," + dtype2 + ">";
                    cb.append("Map<String, dynamic> m" + j.getName() + " = Map<String, dynamic>();\n");

                    if (t2.equals(ProtoType.JSON_OBJECT)) {
                        cb.append(j.getName() + ".forEach((k,v)=> m" + j.getName() + "[k]=v.toJson());\n");
                    } else {
                        cb.append(j.getName() + ".forEach((k,v)=> m" + j.getName() + "[k]=v);\n");
                    }
                }
            }

            cb.append("return " + (!isPrimitiveDartType(i.getResult()) ? "WrappedObject.of(await " : "await ("));
            cb.append("ChimeraSocketHelper." + (i.isBigJob() ? "invokeBigJob" : "invoke") + "(").append("\"" + i.getName() + "\"").append(",");
            cb.append("<dynamic>[");
            cc = new StringBuilder();

            for (ProtoParam j : i.getParams()) {

                if (j.getType().equals(ProtoType.JSON_LIST)) {
                    cc.append(",");
                    cc.append("l" + j.getName());
                } else if (j.getType().equals(ProtoType.JSON_MAP)) {
                    cc.append(",");
                    cc.append("m" + j.getName());
                } else {
                    cc.append(",");
                    cc.append(isPrimitiveDartType(j.getType()) ? j.getName() : (j.getName() + ".toJson()"));
                }


            }

            cb.append(cc.length() > 0 ? cc.substring(1) : cc.toString());
            cb.append("]))");

            if (i.getT1() != null && (i.getResult().equals(ProtoType.JSON_LIST) || i.getResult().equals(ProtoType.JSON_MAP))) {

                if (i.getResult().equals(ProtoType.JSON_LIST)) {
                    cb.append(!isPrimitiveDartType(i.getResult()) ? ("." + (prim(i.getT1()).startsWith("!") ? "get" : "getList<" + prim(i.getT1()) + ">") + "(listType: '" + prim(i.getT1()) + "') as " + returnType) : ("as " + returnType));

                } else {
                    cb.append(!isPrimitiveDartType(i.getResult()) ? ("." + (prim(i.getT2()).startsWith("!") ? "get" : "getMap<" + prim(i.getT2()) + ">") + "(listType: '" + prim(i.getT2()) + "') as " + returnType) : ("as " + returnType));
                }

            } else {
                cb.append(!isPrimitiveDartType(i.getResult()) ? (".get() as " + returnType) : ("as " + returnType));

            }
            cb.append(";}catch(e){ print('Failed to convert result -> " + returnType + ": (probably null) $e'); return null; }}");

            return cb.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String prim(String t1) {
        try {
            String pref = "";
            Class<?> rc = Class.forName(t1);

            if (rc.equals(String.class) || rc.isPrimitive() || rc.equals(Boolean.class)
                    || rc.equals(Integer.class)
                    || rc.equals(Long.class)
                    || rc.equals(Double.class)) {
                pref = "!";
            }

            return pref + dartTypeStatic(ProtoType.of(rc), t1, rc, null, null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return t1;
    }

    /**
     * @param pkg
     * @param className
     * @return
     */
    public KMap<String, String> exportJava(String pkg, String className) {
        KMap<String, String> result = new KMap<>();
        KMap<String, KMap<String, KList<String>>> classMap = new KMap<>();

        for (ProtoFunction i : functions) {
            if (!classMap.containsKey(i.getType())) {
                classMap.put(i.getType(), new KMap<>());
            }

            if (!classMap.get(i.getType()).containsKey(i.getService())) {
                classMap.get(i.getType()).put(i.getService(), new KList<>());
            }

            classMap.get(i.getType()).get(i.getService()).add(exportJavaFunction(i));
        }

        for (String i : dartFunctionFiles.k()) {
            String vv = exportDartToJava(i, dartFunctionFiles.get(i), classMap);
            String x = vv.split("\\Q\\--==--&&--==--\\\\E")[0];
            result.put("dname." + i, vv.split("\\Q\\--==--&&--==--\\\\E")[1]);
            result.put("dart." + i, x);
        }

        StringBuilder cb = new StringBuilder();
        String nl = "\n";
        String nl2 = nl + nl;
        //@builder
        cb.append("package " + pkg + ";" + nl2);
        cb.append("/**" + nl);
        cb.append(" * This was generated by Chimera ProtoGen on " + new Date().toString() + nl);
        cb.append(" */" + nl);
        cb.append("public class " + className + nl);
        cb.append("{" + nl);

        for (String protoType : classMap.keySet()) {
            cb.append("/**" + nl);
            cb.append(" * Represents the " + protoType + " protocol type." + nl);
            cb.append(" * Service is for inter-communication between backend services" + nl);
            cb.append(" * Gateway is for clients running server functions, keep authentication & security in mind!" + nl);
            cb.append(" * Client is for server calling client functions. The implementation server side is ignored." + nl);
            cb.append(" */" + nl);
            cb.append("public static class " + protoType.toUpperCase() + nl);
            cb.append("{" + nl);

            for (String protoService : classMap.get(protoType).keySet()) {
                cb.append("/**" + nl);
                cb.append(" * Represents the " + protoService + " service via the " + protoType + " protocol type." + nl);
                cb.append(" * Service is for inter-communication between backend services" + nl);
                cb.append(" * Gateway is for clients running server functions, keep authentication & security in mind!" + nl);
                cb.append(" * Client is for server calling client functions. The implementation server side is ignored." + nl);
                cb.append(" */" + nl);
                cb.append("public static class " + Form.capitalize(protoService) + nl);
                cb.append("{" + nl);

                for (String func : classMap.get(protoType).get(protoService)) {
                    cb.append(func);
                }

                cb.append("}" + nl);
            }

            cb.append("}" + nl);
        }

        cb.append("}" + nl);

        //@done
        result.put("src", cb.toString());
        return result;
    }

    private String exportDartToJava(String i, File path, KMap<String, KMap<String, KList<String>>> classMap) {
        String projectName = i;
        File src = dartFunctionFiles.get(i);
        StringBuilder dp = new StringBuilder();
        String attempt = path.getAbsolutePath().toString().split("\\Q" + File.separator + "lib" + File.separator + "\\E")[1];
        attempt = attempt.replaceAll("\\Q" + File.separator + "\\E", "/");
        attempt = "import 'package:" + i + "/" + attempt + "';";
        String dartClassName = null;
        dp.append(attempt + "\n");
        dp.append("import 'package:hawkeye/chimera/protocol.dart';\n");
        dp.append("import 'package:hawkeye/hawkeye.dart';\n");
        dp.append("class INTERNALChimeraClientFunctionInvoker extends DefaultInvoker{\n");
        StringBuilder ddp = new StringBuilder();
        ddp.append("@override\n");
        ddp.append("bool hasFunction(String func){\n");
        dp.append("@override\n");
        dp.append("dynamic invokeClientFunction(String func, List<dynamic> params){\n");
        if (!classMap.containsKey(EDX.TYPE_CLIENT)) {
            classMap.put(EDX.TYPE_CLIENT, new KMap<>());
        }

        if (!classMap.get(EDX.TYPE_CLIENT).containsKey(Form.capitalize(projectName))) {
            classMap.get(EDX.TYPE_CLIENT).put(Form.capitalize(projectName), new KList<>());
        }

        try {
            String dsrc = IO.readAll(src);
            KList<String> functionHeaders = new KList<>();
            KList<String> tokens = new KList<String>();

            for (String a : dsrc.split("\\Q\n\\E")) {
                for (String b : a.split("\\Q;\\E")) {
                    if (b.trim().isEmpty()) {
                        continue;
                    }

                    tokens.add(b.trim());
                }
            }

            String last = null;

            for (String a : dsrc.split("\\Q\n\\E")) {
                if (a.trim().startsWith("class")) {
                    dartClassName = a.trim().split("\\Q \\E")[1].trim();
                    L.i("Identified " + i + " Client Functions Class as " + dartClassName);
                }
            }

            for (String a : tokens) {
                if (a.startsWith("static") && a.contains("(") && a.contains(")")) {
                    String methodReturnType = a.split("\\Q \\E")[1];
                    String methodName = a.split("\\Q \\E")[2].split("\\Q(\\E")[0];

                    if (methodName.startsWith("_")) {
                        L.v("Skipping Dart Method: " + a + " (it's private)");
                        continue;
                    }

                    KList<String> a1 = new KList<String>(a.split("\\Q(\\E"));
                    a1.remove(0);
                    String par = "(" + a1.toString("(");
                    a1 = new KList<String>(par.split("\\Q)\\E"));

                    if (a1.size() > 1) {
                        a1.removeLast();
                        par = a1.toString(")") + ")";
                    }

                    KList<DartParam> params = new KList<>();
                    par = par.replaceAll("\\Q(\\E", "").replaceAll("\\Q)\\E", "").trim();

                    if (!par.isEmpty()) {
                        if (par.contains(":") || par.contains("{") || par.contains("}")) {
                            L.f("Protogen does not support optional parameters, or mapped params from dart to java. (" + methodName + "(!!!) in " + projectName + ")");
                            continue;
                        }

                        if (par.contains(",")) {
                            for (String pp : par.split("\\Q,\\E")) {
                                if (pp.trim().isEmpty()) {
                                    continue;
                                }

                                String type = pp.trim().split("\\Q \\E")[0].trim();
                                String name = pp.trim().split("\\Q \\E")[1].trim();
                                params.add(new DartParam(name, type));
                            }
                        } else {
                            String type = par.trim().split("\\Q \\E")[0].trim();
                            String name = par.trim().split("\\Q \\E")[1].trim();
                            params.add(new DartParam(name, type));
                        }
                    }

                    Class<?> jreturn = javaClassForDartType(methodReturnType);
                    KList<ProtoParam> pars = new KList<>();

                    for (DartParam d : params) {
                        Class<?> jt = javaClassForDartType(d.getType());
                        //@builder
                        pars.add(ProtoParam.builder()
                                .name(d.getName())
                                .realType(jt.getCanonicalName())
                                .type(ProtoType.of(jt))
                                .build());
                        //@done
                    }

                    //@builder
                    ProtoFunction f = ProtoFunction.builder()
                            .resultType(jreturn.getCanonicalName())
                            .result(ProtoType.of(jreturn))
                            .name(methodName)
                            .params(pars)
                            .build();
                    //@done

                    ddp.append("if(func == '" + f.getName() + "'){return true;}\n");

                    dp.append("if(func == '" + f.getName() + "'){\n");
                    dp.append("if(params.length != " + f.getParams().size() + "){\n");
                    dp.append("print('ERROR: Function $func requires " + f.getParams().size() + " parameters. ${params.length} was provided instead.');\n");
                    dp.append("return null;");
                    dp.append("}\n");
                    dp.append("return ");
                    dp.append(dartClassName + "." + f.getName() + "(");
                    StringBuilder ff = new StringBuilder();
                    int m = 0;
                    for (ProtoParam po : f.getParams()) {
                        ff.append(", ");
                        String dartType = params.get(m).getType();
                        ff.append("params[" + m + "] as " + dartType);
                        m++;
                    }
                    dp.append(ff.length() == 0 ? ff.toString() : ff.substring(2));
                    dp.append(");\n");
                    dp.append("}\n");

                    StringBuilder cb = new StringBuilder();
                    cb.append("/**\n");
                    cb.append(" * Invokes " + methodName + "(" + par + ") on the client connected to the socket id __session__" + "\n");
                    cb.append(" * @param __session__ The session id for the connected client.\n");

                    m = 0;
                    for (ProtoParam po : f.getParams()) {
                        try {
                            cb.append(" * @param " + po.getName() + " The " + params.get(m).getType() + " -> " + Class.forName(po.getRealType()).getSimpleName() + " parameter in dart " + "\n");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        m++;
                    }

                    cb.append(" * @param blind If blind is set to true, the call is done async and a null or default result is returned.\n");
                    try {
                        cb.append(" * @return Returns the " + methodReturnType + " -> " + Class.forName(f.getFixedResult()).getSimpleName() + " value if not blind." + "\n");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    cb.append(" */\n");
                    cb.append("public static " + f.getFixedResult() + " " + f.getName() + "(");

                    StringBuilder pb = new StringBuilder();

                    //@builder
                    for (ProtoParam pa : new KList<ProtoParam>().qadd(ProtoParam.builder()
                            .name("__session__")
                            .type(ProtoType.ofSilent(String.class))
                            .realType(String.class.getCanonicalName())
                            .build())
                            .add(f.getParams())
                            .qadd(ProtoParam.builder()
                                    .name("blind")
                                    .type(ProtoType.ofSilent(Boolean.class))
                                    .realType(Boolean.class.getCanonicalName())
                                    .build()))
                    //@done
                    {
                        pb.append(", ").append(pa.getFixedType() + " " + pa.getName());
                    }

                    cb.append(pb.length() > 0 ? pb.substring(2) : pb.toString());
                    cb.append("){\n");
                    cb.append("return ");
                    cb.append("(" + jreturn.getCanonicalName() + ")");
                    cb.append("EDN.SERVICE.Gateway.invokeClientObject(__session__, ");
                    cb.append(FunctionReference.class.getCanonicalName() + ".builder().function(\"" + f.getName() + "\")");
                    cb.append(".params(new " + KList.class.getCanonicalName() + "<Object>()");

                    for (ProtoParam pa : f.getParams()) {
                        cb.append(".qadd(" + pa.getName() + ")");
                    }

                    cb.append(").build()");
                    cb.append(", \"" + jreturn.getCanonicalName() + "\", blind);");

                    cb.append("}\n");

                    String jmethod = cb.toString();
                    L.i("Generated Java -> Dart " + methodName + "(...)");
                    classMap.get(EDX.TYPE_CLIENT).get(Form.capitalize(projectName)).add(jmethod);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        dp.append("print('ERROR: Unknown " + i + " function: $func');\n");
        dp.append("return null;\n");
        dp.append("}\n");
        ddp.append("return false;}\n");
        dp.append(ddp.toString());
        dp.append("}\n");


        return dp.toString() + "\\--==--&&--==--\\" + dartClassName;
    }

    private Class<?> javaClassForDartType(String dartType) {
        if (dartType.equals("int")) {
            return Long.class;
        }

        if (dartType.equals("String")) {
            return String.class;
        }

        if (dartType.equals("double")) {
            return Double.class;
        }

        if (dartType.equals("bool")) {
            return Boolean.class;
        }

        Class<?> f = realDobjects.get(dartType);

        if (f == null) {
            L.f("Can't identify java class for given dart type: " + dartType);
        }

        return f;
    }

    private String exportJavaFunction(ProtoFunction f) {
        StringBuilder cb = new StringBuilder();
        StringBuilder pb = new StringBuilder();
        String nl = "\n";
        String nl2 = nl + nl;

        //================== JDOC ===========
        cb.append("    /**" + nl);
        cb.append("     * Invokes " + f.getName() + " on the " + f.getService() + " service" + nl);

        if (f.isDownstreamResult()) {
            cb.append("     * This function is a downstream function meaning it can bend streams around multiple services.");
        }

        cb.append("     * " + f.getDescription() + nl);

        for (ProtoParam j : f.getParams()) {
            cb.append("     * @param " + j.getName() + " " + j.getDescription() + nl);
        }

        cb.append("     */" + nl);
        //================== END JDOC ===========
        cb.append("    public static " + f.getFixedResult() + " " + f.getName() + "(");

        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getFixedType() + " " + i.getName());
        }

        String p = pb.toString();

        if (p.length() > 1) {
            cb.append(p.substring(2));
        }
        cb.append(")");
        cb.append(" {");

        cb.append(O.class.getCanonicalName() + "<" + f.getFixedResult() + "> result = new " + O.class.getCanonicalName() + "<>();");
        cb.append("((");
        cb.append(ChimeraBackendService.class.getCanonicalName() + ")");
        cb.append(Chimera.class.getCanonicalName() + ".delegate)");
        cb.append(".serviceWork(() -> result.set(");
        cb.append("((");
        cb.append(ChimeraBackendService.class.getCanonicalName() + ")");
        cb.append(Chimera.class.getCanonicalName() + ".delegate)");
        cb.append(f.isDownstreamResult() ? (".invokeDownstreamFunction(\"" + f.getName() + "\"") : (".invokeFunction(\"" + f.getName() + "\""));
        pb = new StringBuilder();

        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getName());
        }

        if (f.getParams().isNotEmpty()) {
            cb.append(pb.toString());
        }

        cb.append("))");
        cb.append(", \"" + f.getService() + "\");");

        if ((f.getResult().equals("boolean") || f.getResult().equals(f.getFixedResult())) && f.getFixedResult().equals(Boolean.class.getCanonicalName())) {
            cb.append(" return result.get() == null || result.get();");
        } else {
            cb.append(" return result.get();");
        }

        cb.append("}" + nl2);


        if (f.getType().equals(EDX.TYPE_SERVICE) && f.getResult().equals(ProtoType.VOID) || f.getResult().equals(ProtoType.BOOLEAN) || f.getResult().equals(ProtoType.INT)) {
            cb.append(exportJavaFunctionScheduled(f));
        }

        return cb.toString();
    }

    private String exportJavaFunctionScheduled(ProtoFunction f) {
        StringBuilder cb = new StringBuilder();
        StringBuilder pb = new StringBuilder();
        String nl = "\n";
        String nl2 = nl + nl;

        //================== JDOC ===========
        cb.append("    /**" + nl);
        cb.append("     * Schedules " + f.getName() + " on any " + f.getService() + " service to be done at a scheduled time." + nl);

        cb.append("     * " + f.getDescription() + nl);

        for (ProtoParam j : f.getParams()) {
            cb.append("     * @param " + j.getName() + " " + j.getDescription() + nl);
        }

        cb.append("     */" + nl);
        //================== END JDOC ===========
        cb.append("    public static void schedule" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1) + "(");

        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getFixedType() + " " + i.getName());
        }

        pb.append(", long deadline, long ttl");

        String p = pb.toString();

        if (p.length() > 1) {
            cb.append(p.substring(2));
        }
        cb.append(")");
        cb.append(" {");
        cb.append(nl);
        cb.append(Chimera.class.getCanonicalName() + ".delegate.getServiceDatabase().setAsync(");
        cb.append(ServiceJob.class.getCanonicalName() + ".builder().service(\"" + f.getService() + "\").deadline(deadline).ttl(ttl).function(\"" + f.getName() + "\").id(" + ID.class.getCanonicalName() + ".randomUUID().toString())");
        cb.append(".build().encodeParameters(new Object[]{");
        pb = new StringBuilder();
        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getName());
        }
        p = pb.toString();

        if (p.length() > 1) {
            cb.append(p.substring(2));
        } else {
            cb.append(p);
        }
        cb.append("}));}").append(nl2);
        cb.append(exportJavaFunctionScheduledLazy(f));

        return cb.toString();
    }

    private String exportJavaFunctionScheduledLazy(ProtoFunction f) {
        StringBuilder cb = new StringBuilder();
        StringBuilder pb = new StringBuilder();
        String nl = "\n";
        String nl2 = nl + nl;

        //================== JDOC ===========
        cb.append("    /**" + nl);
        cb.append("     * Schedules " + f.getName() + " on any " + f.getService() + " service to be done at a scheduled time." + nl);

        cb.append("     * " + f.getDescription() + nl);

        for (ProtoParam j : f.getParams()) {
            cb.append("     * @param " + j.getName() + " " + j.getDescription() + nl);
        }

        cb.append("     */" + nl);
        //================== END JDOC ===========
        cb.append("    public static void schedule" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1) + "(");

        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getFixedType() + " " + i.getName());
        }

        pb.append(", long within");

        String p = pb.toString();

        if (p.length() > 1) {
            cb.append(p.substring(2));
        } else {
            cb.append(p);
        }
        cb.append(")");
        cb.append(" {");
        cb.append(nl);
        cb.append("schedule" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1) + "(");

        pb = new StringBuilder();

        for (ProtoParam i : f.getParams()) {
            pb.append(", ");
            pb.append(i.getName());
        }

        p = pb.toString();

        if (p.length() > 1) {
            p = p.substring(2);
        }

        if (p.length() > 1) {
            p += ", ";
        }

        p += (M.class.getCanonicalName() + ".ms() + within, " + M.class.getCanonicalName() + ".ms()");

        cb.append(p);
        cb.append(");");
        cb.append("}").append(nl2);

        return cb.toString();
    }
}
