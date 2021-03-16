package art.arcane.chimera.gateway;

import art.arcane.archon.data.ArchonResult;
import art.arcane.archon.element.ElementList;
import art.arcane.archon.server.ArchonService;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.object.Listener;
import art.arcane.chimera.core.object.Session;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.chimera.core.protocol.generation.FunctionReference;
import art.arcane.chimera.core.protocol.generation.GatewayFunction;
import art.arcane.chimera.core.protocol.generation.ServiceFunction;
import art.arcane.chimera.gateway.net.GatewayClient;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.ID;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.service.QuillService;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class ProtoGateway extends QuillService {
    private int listenerCleanupDaysEviction = 31;

    @GatewayFunction
    public Boolean ping() {
        return true;
    }

    @GatewayFunction
    public String getSessionId() {
        return EDX.getContext().getSessionId();
    }

    @GatewayFunction
    public Boolean isRegistered(ID id) {
        return Listener.builder().id(id).build().exists();
    }

    @GatewayFunction
    public ID registerListener(ID target) {
        ID id = new ID();
        Listener.builder().id(id).target(target).session(getSessionId()).build().push();
        return id;
    }

    @ServiceFunction
    public KList<String> getSessionsListening(ID target) {
        KList<String> sessions = new KList<>();
        ((ChimeraBackendService) Quill.delegate).getDatabase().query("SELECT DISTINCT `session` FROM `listener` WHERE `target` = '" + target.toString() + "';").forEachRow((i) -> sessions.add(i.getString(0)));
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
        return Listener.builder().id(id).build().archon(((ChimeraBackendService) Quill.delegate).getDatabase()).delete();
    }

    @ServiceFunction
    public Integer unregisterListenersBySession(String id) {
        return ((ChimeraBackendService) Quill.delegate).getDatabase().update("DELETE FROM `listener` WHERE `session` = '" + id + "';");
    }

    @GatewayFunction
    public Integer unregisterAll() {
        return ((ChimeraBackendService) Quill.delegate).getDatabase().update("DELETE FROM `listener` WHERE `session` = '" + getSessionId() + "';");
    }

    @GatewayFunction
    public Integer unregisterAllWithTarget(ID target) {
        return ((ChimeraBackendService) Quill.delegate).getDatabase().update("DELETE FROM `listener` WHERE `target` = '" + target.toString() + "' AND `session` = '" + getSessionId() + "';");
    }

    @ServiceFunction
    public KList<Session> getSessionsByUser(ID user) {
        Session s = Session.builder().build().archon(((ChimeraBackendService) Quill.delegate).getDatabase());
        ElementList<Session> el = s.allWhere("`user` = '" + user.toString() + "'");
        return el.toList();
    }

    @ServiceFunction
    public Integer cleanupDeadSessions() {
        KList<String> gateways = new KList<>();
        ArchonService a = Chimera.archon;
        ArchonResult r = a.query("SELECT `id` FROM `service` WHERE `type` = 'gateway'");
        r.forEachRow((i) -> gateways.add("`gateway` != '" + i.getString(0) + "'"));
        return a.update("DELETE FROM `session` WHERE " + gateways.toString(" AND ") + ";");
    }

    @ServiceFunction
    public Integer cleanupDeadListeners() {
        return ((ChimeraBackendService) Quill.delegate).getDatabase().update("DELETE FROM `listener` WHERE `time` < " + (M.ms() - TimeUnit.DAYS.toMillis(listenerCleanupDaysEviction)));
    }

    @ServiceFunction
    public KList<Session> getSessionsByToken(ID token) {
        Session s = Session.builder().build().archon(((ChimeraBackendService) Quill.delegate).getDatabase());
        ElementList<Session> el = s.allWhere("`token` = '" + token.toString() + "'");
        return el.toList();
    }

    @ServiceFunction
    public Session getFirstSessionByUser(ID user) {
        Session s = Session.builder().build().archon(((ChimeraBackendService) Quill.delegate).getDatabase());

        if (s.where("user", user.toString())) {
            return s;
        }

        return null;
    }

    @ServiceFunction
    public Session getFirstSessionByToken(ID token) {
        Session s = Session.builder().build().archon(((ChimeraBackendService) Quill.delegate).getDatabase());

        if (s.where("token", token.toString())) {
            return s;
        }

        return null;
    }

    @ServiceFunction
    public Session getSessionByID(String id) {
        Session s = Session.builder().id(ID.fromString(id)).build().archon(((ChimeraBackendService) Quill.delegate).getDatabase());

        if (s.pull()) {
            return s;
        }

        return null;
    }

    @ServiceFunction
    public Boolean pushContext(ChimeraContext context) {
        try {
            GatewayClient c = ((GatewayService) Quill.delegate).getWebSocketService().getGateway().getClient(context.getSessionId());
            c.setContext(context);

            return true;
        } catch (Throwable e) {
            L.ex(e);
        }

        return false;
    }

    @ServiceFunction
    public Object invokeClientObject(String sessionId, FunctionReference f, String expectedReturn, Boolean blind) {
        GatewayClient c = ((GatewayService) Quill.delegate).getWebSocketService().getGateway().getClient(sessionId);

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
