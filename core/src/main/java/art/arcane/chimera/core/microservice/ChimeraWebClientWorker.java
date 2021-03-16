package art.arcane.chimera.core.microservice;

import art.arcane.quill.logging.L;
import art.arcane.quill.service.QuillService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ChimeraWebClientWorker extends QuillService {

    private transient OkHttpClient client;
    private long timeoutMinutes = 5;
    private static final Runnable DEFAULT_ONFAIL = () ->
    {
    };

    @Override
    public void onEnable() {
        //@builder
        client = new OkHttpClient.Builder()
                .callTimeout(timeoutMinutes, TimeUnit.MINUTES)
                .connectTimeout(timeoutMinutes, TimeUnit.MINUTES)
                .readTimeout(timeoutMinutes, TimeUnit.MINUTES)
                .writeTimeout(timeoutMinutes, TimeUnit.MINUTES)
                .build();
        //@done
    }

    @Override
    public void onDisable() {

    }

    public String request(String url) {
        return request(url, false, DEFAULT_ONFAIL);
    }

    public String request(String url, boolean suppress) {
        return request(url, suppress, DEFAULT_ONFAIL);
    }

    public String request(String url, boolean suppress, Runnable onFail) {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            onFail.run();

            if (!suppress) {
                L.ex(e);
            }
        }

        return null;
    }

    public InputStream requestDownstream(String url, boolean suppress, Runnable onFail) {
        Request request = new Request.Builder().url(url).build();

        try {
            return client.newCall(request).execute().body().byteStream();
        } catch (Throwable e) {
            onFail.run();
            if (!suppress) {
                L.ex(e);
            }
        }

        return null;
    }
}
