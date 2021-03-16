package art.arcane.chimera.core.microservice;

import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChimeraWebImpatientClientWorker extends QuillService {
    private transient OkHttpClient client;
    private long timeoutMs = 150;

    @Override
    public void onEnable() {
        //@builder
        client = new OkHttpClient.Builder()
                .callTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();
        //@done
    }

    @Override
    public void onDisable() {

    }

    public String request(String url) {
        return request(url, false);
    }

    public String request(String url, boolean suppress) {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            if (!suppress) {
                L.ex(e);
            }
        }

        return null;
    }
}
