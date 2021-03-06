package rest;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(exceptions.GenericExceptionMapper.class);
        resources.add(exceptions.HobbyNotFoundMapper.class);
        resources.add(exceptions.MalformedRequestMapper.class);
        resources.add(exceptions.PersonNotFoundMapper.class);
        resources.add(exceptions.ZipcodeNotFoundMapper.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
        resources.add(rest.HobbyResource.class);
        resources.add(rest.PersonResource.class);
        resources.add(rest.ZipcodeResource.class);
        resources.add(utils.CorsRequestFilter.class);
        resources.add(utils.CorsResponseFilter.class);
    }

}
