package de.fstab.microprofile.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Path("/hello-with-annotations")
@ApplicationScoped
public class GreeterResourceWithAnnotations {

    private final Greeter greeter = new Greeter();

    @GET
    @Path("/{name}")
    @Timed(name = "greeter_request_duration_with_annotation")
    @Counted(name = "greeter_request_count_with_annotation")
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