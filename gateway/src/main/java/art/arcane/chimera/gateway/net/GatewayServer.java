package art.arcane.chimera.gateway.net;

import art.arcane.quill.collections.KMap;
import art.arcane.quill.service.QuillServiceWorker;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.websocket.Session;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayServer extends QuillServiceWorker {
    private transient KMap<String, GatewayClient> clients = new KMap<>();

    public void disconnectAll() {
        clients.values().forEach(GatewayClient::disconnect);
    }

    public GatewayClient getClient(String sessionId) {
        if (sessionId == null) {
            throw new RuntimeException("Session ID on getClient is null!");
        }

        return clients.get(sessionId);
    }

    public GatewayClient getClient(Session s) {
        return getClient(s.getId());
    }

    public void onConnect(Session session) {
        clients.put(session.getId(), new GatewayClient(this, session));
    }

    public void onDisconnect(Session session) {
        getClient(session.getId()).disconnect();
        clients.remove(session.getId());
    }

    public void onError(Session session, Throwable e) {
        try {
            getClient(session).error(e);
        } catch (Throwable ef) {
            e.printStackTrace();
        }
    }

    public void onMessage(Session session, String message) {
        getClient(session).receive(message);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
