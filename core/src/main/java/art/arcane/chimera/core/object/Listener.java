package art.arcane.chimera.core.object;

import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.math.M;
import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Dart
@Builder
@Table("listener")
public class Listener {
    @Getter
    @Setter
    @Column(name = "id", type = "VARCHAR(64)", placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Getter
    @Setter
    @Column(name = "target", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private ID target;

    @Getter
    @Setter
    @Column(name = "session", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String session;

    @Getter
    @Setter
    @Builder.Default
    @Column(name = "time", type = "BIGINT", placeholder = "0")
    private long time = M.ms();
}
