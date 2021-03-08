package art.arcane.chimera.core.protocol.generation;

import lombok.Data;

@Data
public class WrappedObject {
    private String type;
    private String generic;
    private Object object;

    public static WrappedObject of(Object o) {
        WrappedObject w = new WrappedObject();
        w.setObject(o);
        w.setType(o.getClass().getSimpleName());

        return w;
    }

    public static WrappedObject ofPrimitiveType(Object o) {
        return ofTyped(o, "!");
    }

    public static WrappedObject ofTyped(Object o, String type) {
        WrappedObject w = new WrappedObject();
        w.setObject(o);
        w.setGeneric(type);
        w.setType(o.getClass().getSimpleName());

        return w;
    }
}
