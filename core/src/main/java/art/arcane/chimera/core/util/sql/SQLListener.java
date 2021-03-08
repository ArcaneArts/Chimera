package art.arcane.quill.sql;

@FunctionalInterface
public interface SQLListener {
    public void handle(String q);
}
