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

package art.arcane.chimera.gateway;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.chimera.core.object.Listener;
import art.arcane.chimera.core.object.ServiceJob;
import art.arcane.chimera.core.object.Session;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.chimera.gateway.net.GatewayWebsocketWorker;
import art.arcane.quill.Quill;
import art.arcane.quill.service.Service;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayService extends ChimeraService {
    private int listenerCleanupMinuteLaziness = 60;
    private int minutesPerSessionCleanup = 60;
    private int sessionCleanupMinuteLaziness = 30;

    public static void main(String[] a) {
        Chimera.start(a);
    }

    @Getter
    @Service
    private GatewayWebsocketWorker webSocketService = new GatewayWebsocketWorker();

    @Service
    @Getter
    @Protocol
    private ProtoGateway gateway = new ProtoGateway();

    @Override
    public void onEnable() {
        super.onEnable();
        scheduleRepeatingJob(() -> EDN.SERVICE.Gateway.scheduleCleanupDeadSessions(TimeUnit.MINUTES.toMillis(sessionCleanupMinuteLaziness)), TimeUnit.MINUTES.toMillis(minutesPerSessionCleanup));
        Quill.postJob(() -> Session.builder().build().archon(Chimera.archon).sync());
        Quill.postJob(() -> Listener.builder().build().archon(Chimera.archon).sync());
        Quill.postJob(() -> ServiceJob.builder().build().archon(Chimera.archon).sync());
        Quill.postJob(() -> HostedService.builder().build().archon(Chimera.archon).sync());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
