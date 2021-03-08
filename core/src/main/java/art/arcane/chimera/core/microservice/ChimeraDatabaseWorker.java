package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.object.ID;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.sql.SQLKit;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The database worker is responsible for managing your database connection(s)
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChimeraDatabaseWorker extends ChimeraServiceWorker {
    private transient ExecutorService executor;
    private transient SQLKit sql;
    private transient ID id = ID.randomUUID();
    private String jdbcAddress = "localhost";
    private String jdbcDatabase = "chimera_" + Chimera.getDelegateModuleName();
    private String jdbcUsername = "chimera";
    private String jdbcPassword = "123123123";
    private int jdbcPort = 3306;
    private long jdbcTimeout = 3000;
    private int jdbcConnections = 1;
    private boolean jdbcLogging = false;
    private String jdbcDriver = "com.mysql.jdbc.Driver";

    static {
        Chimera.fix(com.mysql.jdbc.Driver.class);
        Chimera.fix(com.mysql.cj.conf.url.SingleConnectionUrl.class);
    }

    public ChimeraDatabaseWorker(String db) {
        super();
        this.jdbcDatabase = db;
    }

    public ChimeraDatabaseWorker() {
        super();
    }

    public void setAsync(Object... t) {
        for (Object i : t) {
            executor.submit(() -> setAtomic(i));
        }
    }

    /**
     * Delete multiple objects
     *
     * @param t the objects (typically with the primary key defined, nothing else matters in the object)
     * @return true ONLY if each object was actually deleted.
     */
    public boolean delete(Object... t) {
        for (Object i : t) {
            try {
                if (!getSql().delete(i)) {
                    return false;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Delete an sql object async. CAREFUL WITH THIS. It is very easy to cause unpredictability in your client networking when using this.
     *
     * @param t the object.
     */
    public void deleteAsync(Object... t) {
        J.a(() -> delete(t));
    }

    /**
     * Sets multiple sql objects
     *
     * @param t the objects
     * @return true ONLY if all the objects were set
     */
    public boolean set(Object... t) {
        for (Object i : t) {
            if (!setAtomic(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets the object and returns true if it actually set
     *
     * @param t the object
     * @return true if the object was set
     */
    public boolean setAtomic(Object t) {
        try {
            return getSql().set(t);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Get an object from a row or null if it doesnt exist.
     *
     * @param t   The object class
     * @param p   the constructor parameters to create it
     * @param <T> the assumed type based off of the class
     * @return the object, using your constructor to create it (to fill the primary key), then sql will fill the remaining properties.
     */
    public <T> T get(Class<T> t, Object... p) {
        try {
            return getSql().get(t, p);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }

    /**
     * Validation is important, simply put, validate blank objects when your service starts to ensure tables are properly updated & formatted.
     *
     * @param o the object
     * @return this, (chain)
     */
    public ChimeraDatabaseWorker validate(Object... o) {
        for (Object i : o) {
            try {
                sql.validate(i);
            } catch (Throwable e) {
                L.ex(e);
                Chimera.crash("Failed to validate " + o);
            }
        }

        return this;
    }

    @Override
    public void onEnable() {
        L.i("ENABLE " + id);
        try {
            SQLKit.onHit = () ->
            {
            };
            L.i("START ID IS " + id);
            sql = new SQLKit(getJdbcAddress(), getJdbcDatabase(), getJdbcUsername(), getJdbcPassword(), getJdbcConnections() > 1, getJdbcConnections());
            sql.setConnectionWait(getJdbcTimeout());
            sql.setLogging(isJdbcLogging());
            sql.setDriver(getJdbcDriver());
            sql.setPort(getJdbcPort());
            sql.start();

            executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable var1) {
                    Thread t = new Thread(var1);
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.setName("Chimera Database Async Queue");
                    return t;
                }
            });

            J.a(() ->
            {
                while (true) {
                    J.sleep(60000);

                    try {
                        if (!sql.isValid()) {
                            L.e("Database lost connection");
                            Chimera.crashStack("Database Service Connection Error");
                        }
                    } catch (Throwable e) {
                        Chimera.crashStack("Database Service Connection Error");
                        L.e("Database lost connection");
                        L.ex(e);
                    }
                }
            });

            L.i("Connected to Database");
        } catch (Throwable e) {
            L.f("Failed to connect to Database");
            L.ex(e);
            Chimera.crashStack("Database Service Connection Error");
        }
    }

    @Override
    public void onDisable() {

    }

    /**
     * Get a random row and fill it into this object
     *
     * @param c the object (must be an sql object)
     * @return true if the object was updated to contain a row's information
     */
    public boolean getRandom(Object c) {
        try {
            return getSql().getRand1(c);
        } catch (SQLException ff) {
            ff.printStackTrace();
        }

        return false;
    }

    /**
     * Get a random object where a column has an actual value that matches is
     *
     * @param c   the object to fill
     * @param col the column name
     * @param is  the column condition (what the column's value should be)
     * @return true if the object was filled with a random row's data matching your column condition
     */
    public boolean getRandomWhere(Object c, String col, String is) {
        try {
            return getSql().getWhereRand(c, col, is);
        } catch (SQLException ff) {
            ff.printStackTrace();
        }

        return false;
    }
}
