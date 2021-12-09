package de.fstab.microprofile.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Path("/hello")
@ApplicationScoped
public class GreeterResource {

    private final Greeter greeter = new Greeter();

    @Inject
    MetricRegistry metricRegistry;

    @GET
    @Path("/{name}")
    public Response sayHello(@PathParam("name") String name) {
        long start = System.nanoTime();
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            return Response.status(Integer.parseInt(status)).entity(greeting).build();
        } catch (GreetingNotFoundException e) {
            status = "500";
            return Response.status(Integer.parseInt(status)).entity("").build();
        } finally {
            Tag tag = new Tag("status", status);
            metricRegistry.counter("greeter_request_count", tag).inc();
            metricRegistry.histogram("greeter_request_duration", tag)
                    .update(NANOSECONDS.toMillis(System.nanoTime() - start));
        }
    }
}