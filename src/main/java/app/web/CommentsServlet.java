package app.web;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@MultipartConfig
public class CommentsServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);
    private final CommentDao dao = new CommentDao();
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            var list = dao.list();
            om.writeValue(resp.getWriter(), list);
        } catch (Exception e) {
            log.error("DB error during listing comments", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.trim().isEmpty() || author.length() > 64 ||
            text == null || text.trim().isEmpty() || text.length() > 1000) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Validation error: invalid author or text length");
            return;
        }

        try {
            dao.add(author.trim(), text.trim());
            log.info("New comment added successfully: author='{}', length={}", author.trim(), text.trim().length());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            log.error("DB error during saving comment", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }
}
