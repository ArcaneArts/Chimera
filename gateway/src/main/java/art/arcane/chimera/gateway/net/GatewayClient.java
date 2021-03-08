package art.arcane.chimera.gateway.net;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.chimera.core.protocol.generation.FunctionReference;
import art.arcane.chimera.core.protocol.generation.ProtoExport;
import art.arcane.chimera.core.protocol.generation.ProtoType;
import art.arcane.chimera.core.protocol.generation.WrappedObject;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.collections.KSet;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import com.google.gson.Gson;
import lombok.Data;

import javax.websocket.Session;
import java.io.IOException;

@Data
public class GatewayClient implements IClient {
    private final GatewayServer server;
    private final Session session;
    private final art.arcane.chimera.core.object.Session chimeraSession;
    private ChimeraContext context;
    private final KSet<String> clientFunctionResultWait;
    private final KMap<String, GatewayMessage> clientFunctionResults;

    public GatewayClient(GatewayServer server, Session session) {
        context = ChimeraContext.builder().connectionInitationTime(M.ms())
                .sessionId(session.getId()).build();
        clientFunctionResultWait = new KSet<>();
        clientFunctionResults = new KMap<>();
        this.session = session;
        this.server = server;
        chimeraSession = art.arcane.chimera.core.object.Session.builder()
                .id(session.getId())
                .last(M.ms())
                .gateway(((ChimeraBackendService) Chimera.delegate).getBackendService().getId())
                .token(new ID("none"))
                .user(new ID("none"))
                .build();

        if (context.getAccessToken() != null) {
            chimeraSession.setToken(getContext().getAccessToken().getId());
            chimeraSession.setUser(getContext().getAccessToken().getAccount());
        }

        Chimera.delegate.getServiceDatabase().set(chimeraSession);
    }

    protected void receive(String message) {
        L.i("Received Message: " + message);
        GatewayMessage gm = new Gson().fromJson(message, GatewayMessage.class);
        receiveMessage(gm);
    }

    public void receiveMessage(GatewayMessage gm) {
        updateSessionRow();

        try {
            if (gm.type().equals("pin")) {
                send(gm.reply().type("pon"));
            }

            if (gm.type().equals("fun")) {
                EDX.pushContext(getContext());
                FunctionReference f = (FunctionReference) new Gson().fromJson(new Gson().toJson(gm.data()), FunctionReference.class);
                send(gm.reply().type("ret").data(wrapIfNeeded(
                        EDX.invokeTypeWithContext(getContext(), EDX.TYPE_GATEWAY, f.getFunction(), f.getParams())

                )));
            }

            if (gm.type().equals("fuv")) {
                EDX.pushContext(getContext());
                FunctionReference f = (FunctionReference) new Gson().fromJson(new Gson().toJson(gm.data()), FunctionReference.class);
                EDX.invokeTypeWithContext(getContext(), EDX.TYPE_GATEWAY, f.getFunction(), f.getParams());
            }

            if (gm.type().equals("ret")) {
                if (clientFunctionResultWait.contains(gm.id())) {
                    clientFunctionResultWait.remove(gm.id());
                    clientFunctionResults.put(gm.id(), gm);
                }
            }
        } catch (Throwable e) {
            L.ex(e);
        }
    }

    private void updateSessionRow() {
        boolean wasEmpty = chimeraSession.getUser().toString().equals("none");
        boolean nowSet = false;
        if (context.getAccessToken() != null) {
            if (wasEmpty) {
                nowSet = true;
            }
            chimeraSession.setToken(getContext().getAccessToken().getId());
            chimeraSession.setUser(getContext().getAccessToken().getAccount());
        }

        if (nowSet || M.ms() - chimeraSession.getLast() > 10000) {
            chimeraSession.setLast(M.ms());
            Chimera.delegate.getServiceDatabase().setAsync(chimeraSession);
        }
    }

    private Object wrapIfNeeded(Object v) {
        if (v == null) {
            return null;
        }

        if (!ProtoExport.isPrimitiveDartType(ProtoType.of(v.getClass()))) {
            return WrappedObject.of(v);
        }

        return v;
    }

    public void invokeClientFunctionVoid(FunctionReference f) {
        send(new GatewayMessage().type("fuv").data(f));
    }

    public GatewayMessage invokeClientFunction(FunctionReference f) {
        KList<Object> nd = new KList<>();

        for (Object i : f.getParams()) {
            nd.add(wrapIfNeeded(i));
        }

        f.setParams(nd);
        GatewayMessage gm = new GatewayMessage().type("fun").data(f);
        clientFunctionResultWait.add(gm.id());
        send(gm);
        int waits = 0;
        while (!clientFunctionResults.containsKey(gm.id()) && waits < 60) {
            waits++;
            J.sleep(50);
        }

        return clientFunctionResults.remove(gm.id());
    }

    public void disconnect() {
        try {
            session.close();
            Chimera.delegate.getServiceDatabase().deleteAsync(art.arcane.chimera.core.object.Session.builder().id(getContext().getSessionId()).build());
        } catch (IOException ignored) {

        }
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    public void send(GatewayMessage message) {
        send(new Gson().toJson(message));
    }

    public void sendNow(GatewayMessage message) {
        sendNow(new Gson().toJson(message));
    }

    private void sendNow(String message) {
        updateSessionRow();
        L.i("Sent Message: " + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message) {
        updateSessionRow();
        L.i("Sent Message: " + message);
        session.getAsyncRemote().sendText(message);
    }

    public void error(Throwable e) {
        L.f("Client Error: " + context.getSessionId());
        e.printStackTrace();
    }
}
