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
@Table("jobs")
public class ServiceJob {
    @Getter
    @Setter
    @Column(name = "id", type = "VARCHAR(64)", placeholder = "UNDEFINED", primary = true)
    private String id;

    @Getter
    @Setter
    @Column(name = "service", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String service;

    @Getter
    @Setter
    @Column(name = "function", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String function;

    @Getter
    @Setter
    @Column(name = "parameters", type = "TEXT", placeholder = "UNDEFINED")
    private String parameters;

    @Getter
    @Setter
    @Column(name = "deadline", type = "BIGINT", placeholder = "0")
    private long deadline;

    @Getter
    @Setter
    @Column(name = "ttl", type = "BIGINT", placeholder = "0")
    private long ttl;

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
}
