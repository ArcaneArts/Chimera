package art.arcane.chimera.core.object.guide;

import art.arcane.chimera.core.object.ID;
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
@Table("shelf")
public class Shelf {
    @Getter
    @Setter
    @Column(name = "id", type = ID.SQTYPE, placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Getter
    @Setter
    @Column(name = "name", type = "VARCHAR(64)", placeholder = "Unnamed Guide")
    private String name;

    @Getter
    @Setter
    @Column(name = "color", type = "INT", placeholder = "0")
    private int color;

    @Getter
    @Setter
    @Column(name = "parent", type = ID.SQTYPE, placeholder = "")
    private ID parent;
}
