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

import art.arcane.quill.collections.KMap;
import art.arcane.quill.service.QuillService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.websocket.Session;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayServer extends QuillService {
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
