package de.fstab.dropwizard.demo;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.Timed;
import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GreeterResourceWithAnnotations {

    private final Greeter greeter = new Greeter();

    @GET
    @Timed(name = "greeter_request_duration_with_annotation")
    @Counted(name = "greeter_request_count_with_annotation")
    @Path("/hello-with-annotations/{name}")
    public Response sayHello(@PathParam("name") String name) {
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            return Response.status(Integer.parseInt(status)).entity(greeting).build();
        } catch (GreetingNotFoundException e) {
            status = "500";
            return Response.status(Integer.parseInt(status)).entity("").build();
        }
    }
}