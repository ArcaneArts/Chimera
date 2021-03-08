package art.arcane.chimera.core.protocol.generation;

import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.collections.KList;

import java.lang.reflect.Method;

public class ProtoBuilder {
    public static KList<ProtoFunction> functions(Class<?> c, Object instance) {
        KList<ProtoFunction> f = new KList<>();

        for (Method i : c.getDeclaredMethods()) {
            if (i.isAnnotationPresent(ServiceFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_SERVICE));
            }

            if (i.isAnnotationPresent(GatewayFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_GATEWAY));
            }

            if (i.isAnnotationPresent(ClientFunction.class)) {
                f.add(ProtoFunction.of(i, instance, EDX.TYPE_CLIENT));
            }
        }

        return f;
    }
}
