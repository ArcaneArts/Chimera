package art.arcane.chimera.core.protocol.generation;

import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.logging.L;

public enum ProtoType {
    STRING(String.class),
    INT(int.class, Integer.class),
    LONG(long.class, Long.class),
    BOOLEAN(boolean.class, Boolean.class),
    DOUBLE(double.class, Double.class),
    JSON_LIST(KList.class),
    JSON_MAP(KMap.class),
    JSON_OBJECT(Object.class),
    VOID(void.class, Void.class);

    private KList<Class<?>> types;

    private ProtoType(Class<?>... t) {
        types = new KList<>(t);
    }

    public static ProtoType of(Class<?> c) {
        if (c.equals(Object.class)) {
            return JSON_MAP;
        }

        for (ProtoType i : values()) {
            for (Class<?> j : i.types) {
                if (j.equals(c)) {
                    return i;
                }
            }
        }

        for (ProtoType i : values()) {
            for (Class<?> j : i.types) {
                if (j.isAssignableFrom(c) || c.isAssignableFrom(j)) {
                    return i;
                }
            }
        }

        L.w("Unknown type: " + c.getCanonicalName());

        return JSON_OBJECT;
    }

    public static ProtoType ofSilent(Class<?> c) {
        if (c.equals(Object.class)) {
            return JSON_MAP;
        }
        for (ProtoType i : values()) {
            for (Class<?> j : i.types) {
                if (j.equals(c)) {
                    return i;
                }
            }
        }

        for (ProtoType i : values()) {
            for (Class<?> j : i.types) {
                if (j.isAssignableFrom(c) || c.isAssignableFrom(j)) {
                    return i;
                }
            }
        }

        return JSON_OBJECT;
    }
}
