package de.fstab.dropwizard.demo;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class GreeterResource {

    private final Counter requestCount;
    private final Histogram requestDuration;
    private final Greeter greeter = new Greeter();

    public GreeterResource(MetricRegistry registry) {
        requestCount = registry.counter("greeter_request_count");
        requestDuration = registry.histogram("greeter_request_duration");
    }

    @GET
    @Path("/hello/{name}")
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
            requestCount.inc();
            requestDuration.update(NANOSECONDS.toMillis(System.nanoTime() - start));
        }
    }
}