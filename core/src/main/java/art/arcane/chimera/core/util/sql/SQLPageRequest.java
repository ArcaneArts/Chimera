package art.arcane.quill.sql;

import art.arcane.quill.collections.KList;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class SQLPageRequest<T> {
    private final Class<T> type;
    private final Supplier<T> constructor;
    private int limit = 32;
    private int offset = 0;
    private String where = "WHERE 1";
    private String order = "";

    public long count(SQLKit sql) {
        return sql.countMultiple(type, where);
    }

    public KList<T> query(SQLKit sql) {
        return sql.getMultiple(type, where + order, limit, offset, constructor);
    }
}
