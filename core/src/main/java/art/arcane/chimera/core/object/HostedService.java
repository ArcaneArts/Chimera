package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.quill.collections.ID;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HostedService extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Builder.Default
    @Type("VARCHAR(64)")
    private String type = "";

    @Builder.Default
    @Type("VARCHAR(64)")
    private String address = "localhost";

    @Builder.Default
    @Type("VARCHAR(64)")
    private String dir = "/";

    @Builder.Default
    private long time = 0;

    @Builder.Default
    private int port = 0;

    @Type("SMALLINT")
    @Builder.Default
    public int health = 100;

    public String getURL() {
        return "http://" + address + ":" + port + (dir.startsWith("/") ? dir : ("/" + dir));
    }

    @Override
    public String getTableName() {
        return "service";
    }
}
