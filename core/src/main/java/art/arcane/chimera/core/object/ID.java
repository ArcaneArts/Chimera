package art.arcane.chimera.core.object;

import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.random.RNG;

@Dart
public class ID {
    public static final int SIZE = 64;
    public static final String SQTYPE = "VARCHAR(" + SIZE + ")";
    private String i;

    public ID(String i) {
        this.i = i;
    }

    public ID() {
        this.i = "";
    }

    public static ID from(String string) {
        return new ID(string);
    }

    public String toString() {
        return i;
    }

    public static ID randomUUID() {
        return new ID(RNG.r.sSafe(64));
    }
}
