package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import art.arcane.quill.web.ParcelWebServer;
import art.arcane.quill.web.ParcelWebServerConfiguration;
import lombok.Getter;

public class ChimeraWebServiceWorker extends ChimeraServiceWorker {
    @Getter
    private ParcelWebServerConfiguration webServer = new ParcelWebServerConfiguration();
    private int minPort = 10000;
    private int maxPort = 20000;
    @Getter
    private transient ParcelWebServer server;

    @Override
    public void onEnable() {
        attemptStart(10);
    }

    public int getPort() {
        return webServer.httpPort();
    }

    private void attemptStart(int i) {
        if (i <= 0) {
            Chimera.crashStack("Failed to start web server!");
            return;
        }

        webServer.httpPort(getRandomPort());
        //@builder
        server = new ParcelWebServer()
                .configure(webServer)
                .addParcelables(getClass(), "care.mpm.chimera.core.net.parcels.")
                .start();
        //@done

        if (!server.isActive()) {
            attemptStart(i - 1);
        }

        L.i("Started Web Server on *:" + getWebServer().httpPort());
    }

    private int getRandomPort() {
        return RNG.r.i(minPort, maxPort);
    }

    @Override
    public void onDisable() {
        server.stop();
    }
}
