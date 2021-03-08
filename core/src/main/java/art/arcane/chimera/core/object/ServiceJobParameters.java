package art.arcane.chimera.core.object;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ServiceJobParameters {
    private Object[] parameters;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static ServiceJobParameters fromJson(String json) {
        return new Gson().fromJson(json, ServiceJobParameters.class);
    }
}
