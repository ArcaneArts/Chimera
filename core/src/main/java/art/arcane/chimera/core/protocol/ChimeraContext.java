package art.arcane.chimera.core.protocol;

import art.arcane.chimera.core.object.account.AccessToken;
import art.arcane.quill.math.M;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Builder
public class ChimeraContext {
    private String sessionId;
    private AccessToken accessToken;

    @Builder.Default
    private long connectionInitationTime = M.ms();

    public boolean hasAccessToken() {
        return getAccessToken() != null;
    }

    public void push() {
        EDN.SERVICE.Gateway.pushContext(this);
    }
}
