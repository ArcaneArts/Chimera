package art.arcane.chimera.core.util.web;

import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.io.IO;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.random.RNG;
import com.google.gson.Gson;
import lombok.EqualsAndHashCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@EqualsAndHashCode(callSuper = false)
public abstract class Parcel extends HttpServlet implements Parcelable, ParcelWebHandler {
    public static Runnable onHit = () -> {
    };
    public static boolean LOG_REQUESTS = true;
    private static String hardCache = null;
    private String type;
    private static final long serialVersionUID = 229675254360342497L;

    public Parcel(String parcelType) {
        this.type = parcelType;
    }

    @Override
    public String getParcelType() {
        return type;
    }

    protected int getStatusHTTPCode() {
        return 200;
    }

    protected boolean ensureParameters(HttpServletRequest r, String... pars) {
        for (String i : pars) {
            if (r.getParameter(i) == null) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String x = Thread.currentThread().getName();
        Thread.currentThread().setName("WEB/" + getNode());

        try {
            l("GET", req);
            on(req, resp);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Thread.currentThread().setName(x);
    }

    private void l(String string, HttpServletRequest req) {
        String x = Thread.currentThread().getName();
        Thread.currentThread().setName("WEB/" + getNode());
        if (LOG_REQUESTS) {
            String f = "/" + type + " [";
            Map<String, String[]> m = req.getParameterMap();
            KList<String> g = new KList<>();

            for (String i : m.keySet()) {
                g.add(i);
            }

            f += g.toString(" ") + "]";

            L.v("Handling " + string + " on " + f);
        }
        Thread.currentThread().setName(x);
    }

    protected void write(HttpServletResponse resp, InputStream in) throws IOException {
        IO.fullTransfer(in, resp.getOutputStream(), 8192);
        in.close();
    }

    protected void write(HttpServletResponse resp, String s) throws IOException {
        resp.getWriter().println(s);
    }

    @Override
    public String getNode() {
        return getParcelType();
    }

    public KList<String> getParameterNames() {
        KList<String> m = new KList<String>();

        for (Field i : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(i.getModifiers()) || Modifier.isTransient(i.getModifiers())) {
                continue;
            }

            if (i.isAnnotationPresent(ParcelDescription.class)) {
                m.add(i.getName() + " | " + i.getType().getSimpleName() + " | " + i.getDeclaredAnnotation(ParcelDescription.class).value());
            } else {
                m.add(i.getName() + " | " + i.getType().getSimpleName() + " | No Description Provided");
            }
        }

        return m;
    }

    public abstract Parcelable respond();

    public String getExample() {
        return "https://server.io/" + getNode() + genExampleParams();
    }

    private String genExampleParams() {
        KList<String> ex = new KList<>();

        for (Field i : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(i.getModifiers()) || Modifier.isFinal(i.getModifiers()) || Modifier.isTransient(i.getModifiers())) {
                continue;
            }

            String g = i.getName();

            if (i.getType().equals(boolean.class)) {
                ex.add(g + "=" + RNG.r.b());
            } else if (i.getType().equals(int.class)) {
                ex.add(g + "=" + RNG.r.i(100));
            } else if (i.getType().equals(double.class)) {
                ex.add(g + "=" + RNG.r.d(100));
            } else if (i.getType().equals(float.class)) {
                ex.add(g + "=" + RNG.r.f(100));
            } else if (i.getType().equals(long.class)) {
                ex.add(g + "=" + M.ms());
            } else if (i.getType().equals(short.class)) {
                ex.add(g + "=" + RNG.r.si(50));
            } else if (i.getType().equals(String.class)) {
                ex.add(g + "=text");
            }
        }

        boolean f = true;
        String p = "";

        for (String i : ex) {
            p += (f ? "?" : "&") + i;
            f = false;
        }

        return p;
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean posting) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setStatus(getStatusHTTPCode());
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Cookie, X-CSRF-TOKEN, Accept, Authorization, X-XSRF-TOKEN, Access-Control-Allow-Origin");
        resp.addHeader("Access-Control-Expose-Headers", "Authorization, authenticated");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, OPTIONS");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        String d = null;

        if (onHit != null) {
            onHit.run();
        }

        // Avoid using object serialization if we're hard cached and have a cached value
        if (hardCache != null && getClass().isAnnotationPresent(HardCache.class)) {
            write(resp, hardCache);
            return;
        }

        // See if we're using data parameters
        // web.com/node?d={"p1":"val","p2":"val"}
        if (ensureParameters(req, "d")) {
            d = req.getParameter("d");
        }

        // See if we're using ENCODED data parameters
        // web.com/node?b=eyJwMSI6ICJ2YWwiLCAicDIiOiAidmFsIn0=
        else if (ensureParameters(req, "b")) {
            d = new String(IO.decode(req.getParameter("b")), StandardCharsets.UTF_8);
        }

        // Assume normal param format
        // web.com/node?p1=val&p2=val
        else {
            try {
                Map<String, String[]> m = req.getParameterMap();
                JSONObject j = new JSONObject();

                for (String i : m.keySet()) {
                    try {
                        Field f = getClass().getDeclaredField(i);
                        f.setAccessible(true);
                        if (f.getType().equals(String.class)) {
                            j.put(i, req.getParameter(i));
                        } else if (f.getType().equals(int.class)) {
                            j.put(i, Integer.valueOf(req.getParameter(i)));
                        } else if (f.getType().equals(long.class)) {
                            j.put(i, Long.valueOf(req.getParameter(i)));
                        } else if (f.getType().equals(boolean.class)) {
                            j.put(i, Boolean.valueOf(req.getParameter(i)));
                        }
                    } catch (Throwable e) {
                        JSONObject error = new JSONObject();
                        error.put("error", "Parameter Conversion Error");
                        error.put("type", e.getClass().getCanonicalName());
                        error.put("message", "Could not handle field " + i);
                        write(resp, error.toString());
                        L.ex(e);
                        return;
                    }
                }

                if (!j.keySet().isEmpty()) {
                    d = j.toString(0);
                } else {
                    d = "{}";
                }
            } catch (Throwable e) {
                JSONObject error = new JSONObject();
                error.put("error", "Server Surface Error");
                error.put("type", e.getClass().getCanonicalName());
                error.put("message", "Could not handle web spread request");
                write(resp, error.toString());
                L.ex(e);
                return;
            }
        }

        if (d != null) {
            try {
                Parcelable g = null;

                // If the parcel hit (this) is supposed to receive posts
                // And if the request actually has post data
                // -> Use the respond(stream) instead of respond()
                if (posting && this instanceof UploadParcelable) {
                    InputStream in = req.getInputStream();
                    g = ((UploadParcelable) new Gson().fromJson(d, getClass())).respond(in);
                }

                // Else, use the normal respond()
                else {
                    g = new Gson().fromJson(d, getClass()).respond();
                }

                // If the response parcel is HTML, just render it as such
                if (g instanceof FancyParcelable) {
                    resp.setContentType("text/html");
                    write(resp, ((FancyParcelable) g).getHTML());
                }

                // If the response parcel is Downloadable, send the stream as download
                else if (g instanceof DownloadParcelable) {
                    resp.setContentType("application/octet-stream");
                    resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", ((DownloadParcelable) g).getName()));
                    IO.fullTransfer(((DownloadParcelable) g).getStream(), resp.getOutputStream(), 8192 * 2);
                    resp.getOutputStream().flush();
                    resp.getOutputStream().close();
                }

                // If its a normal parcel, write it as json
                else {
                    if (hardCache == null && getClass().isAnnotationPresent(HardCache.class)) {
                        hardCache = new Gson().toJson(g);
                    }

                    resp.setContentType("application/json");
                    write(resp, new Gson().toJson(g));
                }
            } catch (Throwable e) {
                String pars = "";
                boolean q = false;

                for (String i : new KList<String>(req.getParameterNames())) {
                    q = true;
                    pars += "&" + i + "=" + req.getParameter(i);
                }

                L.w("Server Exception when handling */" + getNode() + (q ? "?" : "") + pars.substring(1).trim());
                L.ex(e);
                JSONObject error = new JSONObject();
                error.put("error", "Server Surface Error");
                error.put("type", e.getClass().getCanonicalName());
                error.put("message", e.getMessage());
                error.put("data", d);
                write(resp, error.toString(0));
                L.ex(e);
                return;
            }
        } else {
            JSONObject error = new JSONObject();
            error.put("error", "Missing Data Parameter (d for data, b for urlencoded) or missing parameters.");
            write(resp, error.toString(0));
            return;
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        l("POST", req);
        if (!J.attempt(() -> handleRequest(req, resp, req.getMethod().equalsIgnoreCase("POST")))) {
            JSONObject error = new JSONObject();
            error.put("error", "Server Response Error");
            write(resp, error.toString(0));
        }
    }

    @Override
    public void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!J.attempt(() -> handleRequest(req, resp, false))) {
            JSONObject error = new JSONObject();
            error.put("error", "Server Response Error");
            write(resp, error.toString(0));
        }
    }

    public String toString() {
        return "Parcel " + getClass().getSimpleName() + " @ /" + getParcelType();
    }
}
