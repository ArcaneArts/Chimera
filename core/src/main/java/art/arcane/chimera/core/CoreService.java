package art.arcane.chimera.core;

import art.arcane.quill.Quill;
import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;

public class CoreService extends QuillService {
    public static void main(String[] a) {
        L.i("Starting Core Service for Utility Purposes Only!");
        Chimera.start(a);
    }

    @Override
    public void onEnable() {
        Quill.crashStack("You cannot start the core service! It is for build tools only!");
    }

    @Override
    public void onDisable() {
        Quill.crashStack("You cannot stop the core service! It is for build tools only!");
    }
}
