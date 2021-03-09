package art.arcane.chimera.core.util.web;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicErrorHandler extends ErrorHandler {
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(200);
        write(response, "{\"type\":\"Error\",\"message\":\"Unknown Node:" + target + "\"}");
    }

    protected void write(HttpServletResponse resp, String s) throws IOException {
        resp.getWriter().println(s);
    }
}
