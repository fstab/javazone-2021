package de.fstab.prometheus_java_client.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class GreeterServlet extends HttpServlet {

    private final Greeter greeter = new Greeter();

    Counter requestCount = Counter.build()
            .name("greeter_requests_total")
            .help("total number of requests")
            .labelNames("status")
            .register();

    Summary latencyQuantiles = Summary.build()
            .name("greeter_requests_duration_summary_seconds")
            .help("duration in seconds")
            .labelNames("status")
            .quantile(0.5, 0.001)
            .quantile(0.95, 0.001)
            .quantile(0.99, 0.001)
            .register();

    Histogram latencyHistogram = Histogram.build()
            .name("greeter_requests_duration_histogram_seconds")
            .help("duration in seconds")
            .labelNames("status")
            .buckets(0.0125, 0.025, 0.0375, 0.05, 0.0625, 0.075, 0.0875, 0.10, 0.1125, 0.125, 0.1375, 0.15, 0.1625, 0.175, 0.1875, 0.20, 0.2125, 0.225, 0.2375, 0.25, 0.2625, 0.275, 0.2875, 0.30)
            .register();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getPathInfo().substring(1); // strip leading slash
        long start = System.nanoTime();
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            response.setStatus(parseInt(status));
            response.getWriter().print(greeting);
        } catch (GreetingNotFoundException e) {
            status = "500";
            response.setStatus(Integer.parseInt(status));
        } finally {
            double durationSeconds = (System.nanoTime() - start) * 1e-9;
            requestCount.labels(status).inc();
            latencyQuantiles.labels(status).observe(durationSeconds);
            latencyHistogram.labels(status).observe(durationSeconds);
        }
    }
}