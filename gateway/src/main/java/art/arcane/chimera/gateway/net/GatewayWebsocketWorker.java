/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    public static GatewayWebsocketWorker instance;
    @Service
    private GatewayServer gateway = new GatewayServer();
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
