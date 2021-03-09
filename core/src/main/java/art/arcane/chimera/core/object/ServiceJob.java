package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
public class ServiceJob extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(64)")
    private String service;

    @Type("VARCHAR(64)")
    private String function;

    @Type("TEXT")
    private String parameters;

    @Builder.Default
    private long deadline = 0;

    @Builder.Default
    private long ttl = 0;

    public ServiceJob encodeParameters(Object[] parameters) {
        setParameters(ServiceJobParameters.builder().parameters(parameters).build().toJson());
        return this;
    }

    public Object[] decodeParameters() {
        try {
            return ServiceJobParameters.fromJson(getParameters()).getParameters();
        } catch (Throwable e) {
            return new Object[0];
        }
    }

    @Override
    public String getTableName() {
        return "jobs";
    }
}
