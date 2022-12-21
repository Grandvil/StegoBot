package ru.ShorthandRestService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import jakarta.servlet.ServletContext;
import ru.Shorthand.Core;
public class ContextListner implements ServletContextListener{
        Core core=null;

        //Run this before web application is started
        @Override
        public void contextInitialized(ServletContextEvent arg0) {
            System.out.println("ServletContextListener session started");
            if (core==null)
            {
                core = new Core();
                //set attrib to accses from restService class
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