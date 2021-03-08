package art.arcane.chimera.gateway.net;

import art.arcane.quill.random.RNG;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
@Data
public class GatewayMessage {
    private String id;
    private String type;
    private Object data;

    public GatewayMessage reply() {
        return new GatewayMessage().id(id());
    }

    public GatewayMessage() {
        id = RNG.r.sSafe(8);
    }
}
