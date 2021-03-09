package art.arcane.chimera.core;

import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.quill.logging.L;

public class CoreService extends ChimeraService {
    public CoreService() {
        super("Core");
    }

    public static void main(String[] a) {
        L.i("Starting Core Service for Utility Purposes Only!");
        Chimera.start(a);
    }

    @Override
    public void onEnable() {
        Chimera.crashStack("You cannot start the core service! It is for build tools only!");
    }

    @Override
    public void onDisable() {
        Chimera.crashStack("You cannot stop the core service! It is for build tools only!");
    }
}
