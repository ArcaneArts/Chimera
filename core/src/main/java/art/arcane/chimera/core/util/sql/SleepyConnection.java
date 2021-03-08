package art.arcane.quill.sql;

import java.sql.Connection;

public interface SleepyConnection extends Connection {
    public long getTimeSinceLastUsage();
}
