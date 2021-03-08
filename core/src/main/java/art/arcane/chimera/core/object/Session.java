package art.arcane.chimera.core.object;

import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Dart
@Builder
@Table("session")
public class Session {
    @Getter
    @Setter
    @Column(name = "id", type = "VARCHAR(64)", placeholder = "UNDEFINED", primary = true)
    private String id;

    @Getter
    @Setter
    @Column(name = "gateway", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String gateway;

    @Getter
    @Setter
    @Column(name = "token", type = ID.SQTYPE, placeholder = "UNDEFINED")
    private ID token;

    @Getter
    @Setter
    @Column(name = "user", type = ID.SQTYPE, placeholder = "UNDEFINED")
    private ID user;

    @Getter
    @Setter
    @Column(name = "last", type = "BIGINT", placeholder = "0")
    private long last;
}
