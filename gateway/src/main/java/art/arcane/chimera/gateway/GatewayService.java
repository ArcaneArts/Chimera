package art.arcane.chimera.gateway;

import art.arcane.archon.element.Element;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.chimera.core.object.Listener;
import art.arcane.chimera.core.object.ServiceJob;
import art.arcane.chimera.core.protocol.EDN;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.chimera.gateway.net.GatewayWebsocketWorker;
import art.arcane.quill.service.ServiceWorker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayService extends ChimeraBackendService {
    private int listenerCleanupMinuteLaziness = 60;
    private int minutesPerSessionCleanup = 60;
    private int sessionCleanupMinuteLaziness = 30;

    public static void main(String[] a) {
        Chimera.start(a);
    }

    @Getter
    @ServiceWorker
    private GatewayWebsocketWorker webSocketService = new GatewayWebsocketWorker();

    @ServiceWorker
    @Getter
    @Protocol
    private ProtoGateway gateway = new ProtoGateway();

    public GatewayService() {
        super("Gateway");
    }

    @Override
    public void onStart() {
        Element.register(ServiceJob.class);
        Element.register(Listener.class);
        Element.register(HostedService.class);
        scheduleRepeatingJob(() -> EDN.SERVICE.Gateway.scheduleCleanupDeadSessions(TimeUnit.MINUTES.toMillis(sessionCleanupMinuteLaziness)), TimeUnit.MINUTES.toMillis(minutesPerSessionCleanup));
        EDN.SERVICE.Gateway.scheduleCleanupDeadListeners(TimeUnit.MINUTES.toMillis(listenerCleanupMinuteLaziness));
    }

    @Override
    public void onStop() {

    }
}
