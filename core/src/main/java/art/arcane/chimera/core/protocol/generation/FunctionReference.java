package art.arcane.chimera.core.protocol.generation;

import art.arcane.quill.collections.KList;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunctionReference {
    private String function;
    private KList<Object> params;
}
