package ru.ShorthandRestService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This class is used to tell to the server that there are REST services.
 * An alternative is to declare a servlet in the web.xml.
 */
@ApplicationPath("rest")
public class RestConfig extends Application {

}
