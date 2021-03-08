package art.arcane.chimera.gateway.net;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/")
public class GatewayEndpoint
{
    private GatewayServer getServer()
    {
        return GatewayWebsocketWorker.instance.getGateway();
    }

    @OnOpen
    public void onOpen(Session session) throws IOException
    {
        getServer().onConnect(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException
    {
        getServer().onMessage(session, message);
    }

    @OnClose
    public void onClose(Session session) throws IOException
    {
        getServer().onDisconnect(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable)
    {
        getServer().onError(session, throwable);
    }
}