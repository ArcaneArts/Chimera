package art.arcane.chimera.core.object;

import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Table("service")
public class HostedService {
    @Getter
    @Setter
    @Column(name = "id", type = "VARCHAR(128)", placeholder = "UNDEFINED", primary = true)
    private String id;

    @Getter
    @Setter
    @Column(name = "type", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String type;

    @Getter
    @Setter
    @Column(name = "address", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String address;

    @Getter
    @Setter
    @Column(name = "dir", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String dir;

    @Getter
    @Setter
    @Column(name = "time", type = "BIGINT", placeholder = "0")
    private long time;

    @Getter
    @Setter
    @Column(name = "port", type = "INT", placeholder = "0")
    private int port;

    @Getter
    @Setter
    @Builder.Default
    @Column(name = "health", type = "INT", placeholder = "100")
    public int health = 100;

    public String getURL() {
        return "http://" + address + ":" + port + (dir.startsWith("/") ? dir : ("/" + dir));
    }

    public String getRedisId() {
        return getType() + "/" + getId();
    }
}
