package app.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent e) {
        try { new CommentDao().init(); }
        catch (Exception ex) { throw new RuntimeException(ex); }
    }
}
