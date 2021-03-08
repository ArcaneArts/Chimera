package art.arcane.chimera.core.net;

import art.arcane.chimera.core.object.HostedService;
import art.arcane.quill.collections.KList;

public class ServiceSet extends KList<HostedService> {
    private int atIndex = 0;

    public HostedService getNextService() {
        if (size() == 0) {
            return null;
        }

        if (!hasIndex(atIndex)) {
            atIndex = 0;
        }

        return get(atIndex++);
    }
}
