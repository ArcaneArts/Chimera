package art.arcane.chimera.gateway;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraServiceWorker;
import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.object.Listener;
import art.arcane.chimera.core.object.Session;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.generation.FunctionReference;
import art.arcane.chimera.core.protocol.generation.GatewayFunction;
import art.arcane.chimera.core.protocol.generation.ServiceFunction;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.chimera.gateway.net.GatewayClient;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ProtoGateway extends ChimeraServiceWorker {
    private int listenerCleanupDaysEviction = 31;

    @GatewayFunction
    public Boolean ping() {
        return true;
    }

    @GatewayFunction
    public String getSessionId() {
        return getContext().getSessionId();
    }

    @GatewayFunction
    public Boolean isRegistered(ID id) {
        try {
            return getServiceDatabase().getSql().exists(Listener.builder().id(id).build());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @GatewayFunction
    public ID registerListener(ID target) {
        ID id = ID.randomUUID();
        getServiceDatabase().set(Listener.builder().id(id).target(target).session(getSessionId()).build());
        return id;
    }

    @ServiceFunction
    public KList<String> getSessionsListening(ID target) {
        KList<String> sessions = new KList<>();

        try {
            ResultSet r = getServiceDatabase().getSql().getConnection().prepareStatement("SELECT DISTINCT `session` FROM `listener` WHERE `target` = '" + target.toString() + "';").executeQuery();

            while (r.next()) {
                sessions.add(r.getString(1));
            }
        } catch (Throwable e) {

        }

        return sessions;
    }

    @ServiceFunction
    public Boolean publishTargetUpdate(ID target) {
        boolean v = false;
        for (String i : getSessionsListening(target)) {
            J.a(() -> {
                Boolean b = EDN.CLIENT.Hawkeye.targetUpdated(i, target.toString(), false);

                if (b == null) {
                    int g = unregisterListenersBySession(i);
                    L.i("Unregistered " + g + " session listener(s) for invalid session " + i);
                }
            });

            v = true;
        }

        return v;
    }

    @GatewayFunction
    public Boolean unregisterListener(ID id) {
        return getServiceDatabase().delete(Listener.builder().id(id).build());
    }

    @ServiceFunction
    public Integer unregisterListenersBySession(String id) {
        try {
            return getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `listener` WHERE `session` = '" + id + "';").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @GatewayFunction
    public Integer unregisterAll() {
        try {
            return getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `listener` WHERE `session` = '" + getSessionId() + "';").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @GatewayFunction
    public Integer unregisterAllWithTarget(ID target) {
        try {
            return getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `listener` WHERE `target` = '" + target.toString() + "' AND `session` = '" + getSessionId() + "';").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @ServiceFunction
    public KList<Session> getSessionsByUser(ID user) {
        Session s = Session.builder().build();

        try {
            KList<Session> sds = new KList<>();
            return getServiceDatabase().getSql().getAllFor(user.toString(), "user", Session.class, sds, () -> Session.builder().build());
        } catch (SQLException v) {
            v.printStackTrace();
        }
        return new KList<>();
    }

    @ServiceFunction
    public Integer cleanupDeadSessions() {
        try {
            KList<String> gateways = new KList<>();

            ResultSet r = getServiceDatabase().getSql().getConnection().prepareStatement("SELECT `id` FROM `service` WHERE `type` = 'gateway'").executeQuery();

            while (r.next()) {
                gateways.add("`gateway` != '" + r.getString(1) + "'");
            }

            return getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `session` WHERE " + gateways.toString(" AND ") + ";").executeUpdate();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return -1;
    }

    @ServiceFunction
    public Integer cleanupDeadListeners() {
        try {

            return getServiceDatabase().getSql().getConnection().prepareStatement("DELETE FROM `listener` WHERE `time` < " + (M.ms() - TimeUnit.DAYS.toMillis(listenerCleanupDaysEviction))).executeUpdate();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return -1;
    }

    @ServiceFunction
    public KList<Session> getSessionsByToken(ID t) {
        Session s = Session.builder().build();

        try {
            KList<Session> sds = new KList<>();
            return getServiceDatabase().getSql().getAllFor(t.toString(), "token", Session.class, sds, () -> Session.builder().build());
        } catch (SQLException v) {
            v.printStackTrace();
        }
        return new KList<>();
    }

    @ServiceFunction
    public Session getFirstSessionByUser(ID user) {

        Session s = Session.builder().build();
        try {
            if (getServiceDatabase().getSql().getWhere(s, "user", user.toString())) {
                return s;
            }
        } catch (SQLException v) {
            v.printStackTrace();
        }

        return null;
    }

    @ServiceFunction
    public Session getFirstSessionByToken(ID t) {
        Session s = Session.builder().build();
        try {
            if (getServiceDatabase().getSql().getWhere(s, "token", t.toString())) {
                return s;
            }
        } catch (SQLException v) {
            v.printStackTrace();
        }

        return null;
    }

    @ServiceFunction
    public Session getSessionByID(String id) {
        Session s = Session.builder().build();
        try {
            if (getServiceDatabase().getSql().getWhere(s, "id", id)) {
                return s;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @ServiceFunction
    public Boolean pushContext(ChimeraContext context) {
        try {
            GatewayClient c = ((ChimeraGatewayService) Chimera.delegate).getWebSocketService().getGateway().getClient(context.getSessionId());
            c.setContext(context);

            return true;
        } catch (Throwable e) {
            L.ex(e);
        }

        return false;
    }

    @ServiceFunction
    public Object invokeClientObject(String sessionId, FunctionReference f, String expectedReturn, Boolean blind) {
        GatewayClient c = ((ChimeraGatewayService) Chimera.delegate).getWebSocketService().getGateway().getClient(sessionId);

        if (c != null) {
            try {
                if (blind) {
                    c.invokeClientFunctionVoid(f);
                    return null;
                }

                Object o = c.invokeClientFunction(f).data();
                Class<?> expect = Class.forName(expectedReturn);
                if (!o.getClass().equals(expect)) {
                    if (o instanceof Number) {
                        if (expect.equals(Long.class)) {
                            return ((Number) o).longValue();
                        }
                        if (expect.equals(Integer.class)) {
                            return ((Number) o).intValue();
                        }

                        if (expect.equals(Double.class)) {
                            return ((Number) o).doubleValue();
                        }
                    } else {
                        return new Gson().fromJson(new Gson().toJson(o), expect);
                    }
                }
                return o;

            } catch (Throwable e) {
                L.ex(e);
            }
        } else {
            L.w("Can't find session " + sessionId);
            // TODO SEND TO ANOTHER GATEWAY SOMEHOW
        }

        return null;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
