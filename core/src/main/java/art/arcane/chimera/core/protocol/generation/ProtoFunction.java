package art.arcane.chimera.core.protocol.generation;

import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.KList;
import art.arcane.quill.logging.L;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

@Data
@Builder
public class ProtoFunction {
    private String name;
    private ProtoType result;
    private String description;
    private String service;
    private String src;
    private String resultType;
    private String type;
    private String t1;
    private String t2;
    @Builder.Default
    private boolean downstreamResult = false;
    @Builder.Default
    private boolean bigJob = false;
    private transient Method knownMethod;
    private transient Object knownInstance;

    @Builder.Default
    private KList<ProtoParam> params = new KList<>();

    public void filterTypes(Object[] array) {
        for (int i = 0; i < array.length; i++) {
            if (params.hasIndex(i)) {
                try {
                    if (array[i] == null) {
                        L.w("Function Call " + getName() + "() Parameter #" + i + " is null!");
                        continue;
                    }

                    array[i] = filterType(array[i], Class.forName(params.get(i).getFixedType()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object filterType(Object object, Class<?> expectedType) {
        if (object == null) {
            throw new RuntimeException("Null Object called on filterType!");
        }

        if (object.getClass().equals(expectedType)) {
            return object;
        }

        if (object instanceof Number) {
            if (expectedType.equals(Integer.class)) {
                return ((Number) object).intValue();
            }

            if (expectedType.equals(Long.class)) {
                return ((Number) object).longValue();
            }

            if (expectedType.equals(Double.class)) {
                return ((Number) object).doubleValue();
            }
        }

        if (object instanceof LinkedTreeMap) {
            return new Gson().fromJson(new Gson().toJson(object), expectedType);
        }

        return object;
    }

    public Object invoke(Object... o) throws Throwable {
        return invokeWithContext(null, o);
    }

    public Object invokeWithContext(ChimeraContext context, Object... o) throws Throwable {
        Object ret = null;

        try {
            filterTypes(o);
        } catch (Throwable e) {
            L.f("Failed to filter parameter types on " + getName() + "()... Parameters Allowed: " + getParams().size() + " Provided: " + o.length + " (of which some may be null, see above)");
            e.printStackTrace();
        }

        try {
            EDX.pushContext(context);
            ret = getKnownMethod().invoke(knownInstance, o);
        } catch (Throwable e) {
            L.ex(e);
            L.f("=================================================");
            L.f("Tried calling " + getName() + "(");
            for (Object i : o) {
                L.f(i.getClass().getCanonicalName() + ": " + i.toString());
            }

            L.f(");");
            L.f("-------------------------------------------------");
            L.f("Actual Signature: " + getName() + "(");

            for (ProtoParam i : params) {
                L.f(i.getRealType());
            }

            L.f(");");
            L.f("=================================================");
            Quill.crash("Failed to invoke function.");
        }

        return ret;
    }

    public InputStream invokeDownstreamWithContext(ChimeraContext context, Object... o) throws Throwable {
        InputStream ret = null;

        filterTypes(o);

        try {
            EDX.pushContext(context);
            ret = (InputStream) getKnownMethod().invoke(knownInstance, o);
        } catch (Throwable e) {
            L.ex(e);
            L.f("=================================================");
            L.f("Tried calling " + getName() + "(");
            for (Object i : o) {
                L.f(i.getClass().getCanonicalName() + ": " + i.toString());
            }

            L.f(");");
            L.f("-------------------------------------------------");
            L.f("Actual Signature: " + getName() + "(");

            for (ProtoParam i : params) {
                L.f(i.getRealType());
            }

            L.f(");");
            L.f("=================================================");
        }

        return ret;
    }

    public String getFixedResult() {
        if (resultType.equals("double")) {
            return Double.class.getCanonicalName();
        }
        if (resultType.equals("int")) {
            return Integer.class.getCanonicalName();
        }
        if (resultType.equals("long")) {
            return Long.class.getCanonicalName();
        }
        if (resultType.equals("boolean")) {
            return Boolean.class.getCanonicalName();
        }
        if (resultType.equals("void")) {
            return Boolean.class.getCanonicalName();
        }

        return resultType;
    }

    public static ProtoFunction of(Method m, Object fromInstance, String type) {
        m.setAccessible(true);
        //@builder
        ProtoFunction f = ProtoFunction.builder()
                .name(m.getName())
                .type(type)
                .bigJob(m.isAnnotationPresent(BigJob.class))
                .resultType(m.getReturnType().getCanonicalName())
                .service(Quill.getDelegateModuleName())
                .downstreamResult(m.getReturnType().equals(InputStream.class))
                .src(m.getDeclaringClass().getCanonicalName())
                .description("No Description Provided")
                .result(ProtoType.of(m.getReturnType()))
                .t1(ProtoExport.listTypeOfJava(m, 0, m.getDeclaringClass().getCanonicalName()))
                .t2(ProtoExport.listTypeOfJava(m, 1, m.getDeclaringClass().getCanonicalName()))
                .build();
        //@done

        for (Parameter i : m.getParameters()) {
            f.getParams().add(ProtoParam.of(i));
        }

        if (!Modifier.isStatic(m.getModifiers())) {
            f.setKnownInstance(fromInstance);
        }

        f.setKnownMethod(m);

        return f;
    }
}
