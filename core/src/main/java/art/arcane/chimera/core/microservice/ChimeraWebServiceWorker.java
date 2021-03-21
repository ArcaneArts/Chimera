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

package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.util.web.ParcelWebServer;
import art.arcane.chimera.core.util.web.ParcelWebServerConfiguration;
import art.arcane.quill.Quill;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import art.arcane.quill.service.QuillService;
import lombok.Getter;

public class ChimeraWebServiceWorker extends QuillService {
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
            Quill.crashStack("Failed to start web server!");
            return;
        }

        webServer.httpPort(getRandomPort());
        //@builder
        server = new ParcelWebServer()
                .configure(webServer)
                .addParcelables(getClass(), "art.arcane.chimera.core.net.parcels.")
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
