package art.arcane.quill.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ParcelWebHandler {
    public String getNode();

    public void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}
