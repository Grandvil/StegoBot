package ru.ShorthandRestService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import jakarta.servlet.ServletContext;
import ru.Shorthand.StegoServiceCore;

public class RestServiceContextListner implements ServletContextListener {
    StegoServiceCore core = null;


    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("ServletContextListener session started");
        if (core == null) {
            core = new StegoServiceCore();

            ServletContext context = arg0.getServletContext();
            context.setAttribute("core", core);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        core.Stop();
        System.out.println("ServletContextListener session destroyed");
    }
}