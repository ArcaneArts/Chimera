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

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/")
public class GatewayEndpoint {
    private GatewayServer getServer() {
        return GatewayWebsocketWorker.instance.getGateway();
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        getServer().onConnect(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        getServer().onMessage(session, message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        getServer().onDisconnect(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        getServer().onError(session, throwable);
    }
}