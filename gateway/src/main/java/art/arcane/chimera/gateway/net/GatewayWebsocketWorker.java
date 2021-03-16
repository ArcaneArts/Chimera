package art.arcane.chimera.gateway.net;

import art.arcane.quill.Quill;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;
import art.arcane.quill.service.Service;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayWebsocketWorker extends QuillService {
    @Service
    private GatewayServer gateway = new GatewayServer();
    public static GatewayWebsocketWorker instance;
    private transient Server server;
    private int port = 8585;
    private String bind = "localhost";

    @Override
    public void onEnable() {
        instance = this;
        server = new Server(getBind(), getPort(), "/", GatewayEndpoint.class);
        try {
            server.start();
        } catch (DeploymentException e) {
            L.ex(e);
            Quill.crash("Deployment Error Websocket Server");
        }
    }

    @Override
    public void onDisable() {
        server.stop();
    }
}
