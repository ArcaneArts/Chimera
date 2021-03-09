package art.arcane.chimera.core.util.web;

import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.format.Form;
import art.arcane.quill.logging.L;
import art.arcane.quill.reaction.O;
import art.arcane.quill.tools.JarTools;
import lombok.Getter;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ParcelWebServer {
    private ParcelWebServerConfiguration config;

    @Getter
    private final KList<Class<? extends Parcelable>> parcelables;
    private Server server;
    @Getter
    private boolean active = false;
    private int port;
    private KList<ParcelListener<Parcelable, Parcelable>> listeners;

    public ParcelWebServer() {
        parcelables = new KList<Class<? extends Parcelable>>();
        config = new ParcelWebServerConfiguration(this);
        port = 80;
        listeners = new KList<>();
    }

    public ParcelWebServer addListener(ParcelListener<Parcelable, Parcelable> p) {
        listeners.add(p);
        return this;
    }

    public String toString() {
        return "JPWS " + (configure().http() ? "HTTP:" + configure().httpPort() + " " : "") + (configure().https() ? "HTTPS:" + configure().httpsPort() + " " : "");
    }

    public void stop() {
        if (server != null && server.isRunning()) {
            try {
                L.i("Stopping " + this);
                server.stop();
                L.i("Stopped " + this);
            } catch (Exception e) {
                L.ex(e);
            }
        }
    }

    public Parcelable hit(Parcelable i, Parcelable o) {
        Parcelable f = o;

        for (ParcelListener<Parcelable, Parcelable> g : listeners) {
            f = g.handle(i, f);
        }

        return f;
    }

    public void generateMarkdownSpec(File md) {
        KMap<String, KMap<String, KList<String>>> ss = new KMap<>();

        try {
            try {
                md.getParentFile().mkdirs();
            } catch (Throwable e) {

            }
            PrintWriter pw = new PrintWriter(md);
            pw.println("# Parcel Spec");
            pw.println();
            pw.println("This file was generated by Shuriken based on all parcel objects in the project.");
            pw.println();

            for (Class<? extends Parcelable> f : parcelables) {
                try {
                    Parcelable p = f.getConstructor().newInstance();
                    String category = "";
                    ParcelRequest aq = f.getDeclaredAnnotation(ParcelRequest.class);
                    ParcelResponse ar = f.getDeclaredAnnotation(ParcelResponse.class);
                    String subcat = "";

                    if (aq != null) {
                        subcat = "Requests";
                        category = "Parcels for " + (aq.value().trim().isEmpty() ? "Uncategorized" : aq.value());
                    } else if (ar != null) {
                        subcat = "Responses";
                        category = "Parcels for " + (ar.value().trim().isEmpty() ? "Uncategorized" : ar.value());
                    } else {
                        subcat = "Other";
                        category = "Uncategorized";
                    }

                    ParcelDescription d = f.getDeclaredAnnotation(ParcelDescription.class);
                    String description = d != null ? d.value() : "No Description Provided";
                    String w = "* [" + p.getClass().getSimpleName() + " at `/" + p.getParcelType() + "`" + "](#" + p.getParcelType() + ") " + description;
                    ss.putThen(category, new KMap<>()).putThen(subcat, new KList<>()).add(w);
                } catch (Throwable e) {
                    L.ex(e);
                }
            }

            pw.println();
            pw.println("| Name      | Contents       |");
            pw.println("|-----------|----------------|");

            ss.k().sort().forEach((l) ->
            {
                O<Integer> c = new O<Integer>();
                c.set(0);
                ss.get(l).v().forEach((bb) -> c.set(c.get() + bb.size()));
                pw.println("| [" + l + "](#" + l.toLowerCase().replaceAll("\\Q \\E", "-") + ") | " + c.get() + " node" + (c.get() == 1 ? "" : "s") + " |");
            });

            pw.println();
            pw.println("---");
            pw.println();

            ss.k().sort().forEach((l) ->
            {
                pw.println("## " + l);
                ss.get(l).k().sort().forEach((v) ->
                {
                    pw.println("### " + v);
                    ss.get(l).get(v).copy().sort().forEach((x) -> pw.println(x));
                });
                pw.println();
            });
            pw.println();

            for (Class<? extends Parcelable> f : parcelables) {
                try {
                    Parcelable p = f.getConstructor().newInstance();
                    ParcelDescription d = f.getDeclaredAnnotation(ParcelDescription.class);
                    ParcelResponseSuccess s = f.getDeclaredAnnotation(ParcelResponseSuccess.class);
                    ParcelResponseError e = f.getDeclaredAnnotation(ParcelResponseError.class);
                    String description = d != null ? d.value() : "No Description Provided";
                    Class<? extends Parcelable> successType = s != null ? s.type() : null;
                    String sType = successType != null ? (successType.equals(FancyParcelable.class)) ? "HTML" : successType.getConstructor().newInstance().getParcelType() : "null";
                    String successDescription = s != null ? s.reason() : "No Description Provided";
                    Class<? extends Parcelable> errorType = e != null ? e.type() : null;
                    String eType = errorType != null ? (successType.equals(FancyParcelable.class)) ? "HTML" : errorType.getConstructor().newInstance().getParcelType() : "null";
                    String errorDescription = e != null ? e.reason() : "No Description Provided";
                    KList<String> parameters = p.getParameterNames();
                    pw.println("## `/" + p.getParcelType() + "`");
                    pw.println();
                    pw.println("**Example** " + p.getExample());
                    pw.println();
                    pw.println("> " + description);
                    pw.println();

                    if (!parameters.isEmpty()) {
                        pw.println("#### Parameters");
                        pw.println();
                        pw.println("| Name      | Type | Description    |");
                        pw.println("|-----------|------|----------------|");

                        for (String i : parameters) {
                            pw.println("| " + i + " |");
                        }

                        pw.println();
                    }

                    if (successType != null) {
                        pw.println("**On Success** Responds with [" + sType + "](#" + sType + ") `" + successDescription + "`");
                        pw.println();
                    }

                    if (errorType != null) {
                        pw.println("**On Error** Responds with [" + eType + "](#" + eType + ") `" + errorDescription + "`");
                        pw.println();
                    }

                    pw.println("---");
                } catch (Throwable e) {
                    L.ex(e);
                }
            }

            pw.close();
        } catch (Throwable ee) {
            L.ex(ee);
        }
    }

    public ParcelWebServer start() {
        stop();
        L.i("Starting " + this);
        server = new Server(new QueuedThreadPool(256, 20, 10000, 16, null, new ThreadGroup("ParcelJWS")));
        ServletContextHandler api = new ServletContextHandler(server, configure().serverPath());
        api.setMaxFormContentSize(configure().maxFormContentSize());

        for (Class<? extends Parcelable> f : parcelables) {
            try {
                Parcel p = (Parcel) f.getConstructor().newInstance();
                api.addServlet(p.getClass(), "/" + p.getParcelType());
            } catch (Throwable e) {
                L.ex(e);
            }
        }

        L.i("Started " + parcelables.size() + " Parcelables");

        api.setErrorHandler(new BasicErrorHandler());

        if (configure().https() && configure().http()) {
            L.i("Configuring " + this + " for HTTP & HTTPS");
            server.setConnectors(new Connector[]{getHTTPConnector(), getHTTPSConnector()});
            this.port = configure().httpsPort();
        } else if (configure().https()) {
            L.i("Configuring " + this + " for HTTPS");
            server.setConnectors(new Connector[]{getHTTPSConnector()});
            this.port = configure().httpsPort();
        } else if (configure().http()) {
            L.i("Configuring " + this + " for HTTP");
            server.setConnectors(new Connector[]{getHTTPConnector()});
            this.port = configure().httpPort();
        } else {
            throw new RuntimeException("Cannot start a web server with both http and https protocols turned off!");
        }

        try {
            server.start();
            L.i(this + " Form Post Max: " + Form.fileSize(configure().maxFormContentSize()));
            L.i(this + " Online!");
            active = true;
        } catch (Exception e) {
            L.ex(e);
            throw new RuntimeException("Failed to start webserver");
        }

        return this;
    }

    private ServerConnector getHTTPConnector() {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(configure().httpPort());
        L.i("Creating a connector for HTTP/" + configure().httpPort());
        return connector;
    }

    @SuppressWarnings("deprecation")
    private ServerConnector getHTTPSConnector() {
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(configure().sslKeystore());
        sslContextFactory.setKeyStorePassword(configure().sslKeystorePassword());
        sslContextFactory.setKeyManagerPassword(configure().sslKeystoreKeyPassword());
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setPort(configure().httpsPort());
        L.i("Creating a connector for HTTPS/" + configure().httpsPort() + " secured with " + configure().sslKeystore() + "/" + configure().sslKeystoreKeyName());

        return sslConnector;
    }

    public ParcelWebServerConfiguration configure() {
        return config;
    }

    public ParcelWebServer configure(ParcelWebServerConfiguration c) {
        config = c;
        return this;
    }

    public ParcelWebServer addParcelables(Class<?> amchorPackage, String packagePrefix) {
        return addParcelables(JarTools.getClassesInPackage(JarTools.getJar(amchorPackage), packagePrefix, Parcelable.class));
    }

    public ParcelWebServer addParcelables(KList<Class<? extends Parcelable>> o) {
        for (Class<? extends Parcelable> i : o) {
            parcelables.add(i);
        }

        return this;
    }

    @SafeVarargs
    public final ParcelWebServer addParcelables(Class<? extends Parcelable>... o) {
        for (Class<? extends Parcelable> i : o) {
            parcelables.add(i);
        }

        return this;
    }

    public int getPort() {
        return port;
    }

    public Parcelable createParcel(String node, KMap<String, String> p) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        for (Class<? extends Parcelable> i : parcelables) {
            Parcelable parcel = i.getConstructor().newInstance();

            if (parcel.getParcelType().equals(node)) {
                for (Field j : parcel.getClass().getDeclaredFields()) {
                    j.setAccessible(true);

                    if (p.containsKey(j.getName())) {
                        String val = p.get(j.getName());

                        if (j.getType().equals(String.class)) {
                            j.set(parcel, val);
                        }

                        if (j.getType().equals(int.class) || j.getType().equals(Integer.class)) {
                            j.set(parcel, Integer.valueOf(val));
                        }

                        if (j.getType().equals(double.class) || j.getType().equals(Double.class)) {
                            j.set(parcel, Double.valueOf(val));
                        }

                        if (j.getType().equals(boolean.class) || j.getType().equals(Boolean.class)) {
                            j.set(parcel, Boolean.valueOf(val));
                        }

                        if (j.getType().equals(float.class) || j.getType().equals(Float.class)) {
                            j.set(parcel, Float.valueOf(val));
                        }

                        if (j.getType().equals(long.class) || j.getType().equals(Long.class)) {
                            j.set(parcel, Long.valueOf(val));
                        }
                    }
                }

                return parcel;
            }
        }

        return null;
    }
}
