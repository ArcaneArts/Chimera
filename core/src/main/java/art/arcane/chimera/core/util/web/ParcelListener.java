package art.arcane.chimera.core.util.web;

@FunctionalInterface
public interface ParcelListener<IN, OUT> {
    public OUT handle(IN in, OUT out);
}
