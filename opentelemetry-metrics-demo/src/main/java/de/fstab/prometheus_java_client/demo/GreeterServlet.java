package de.fstab.prometheus_java_client.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.ExponentialHistogramData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static java.lang.Integer.parseInt;

public class GreeterServlet extends HttpServlet {

    private final LongUpDownCounter activeRequests;
    private final DoubleHistogram duration;
    private final Greeter greeter = new Greeter();

    private Attributes extractAttributes(HttpServletRequest request) {
        String protocol = request.getProtocol();
        if (protocol != null && protocol.startsWith("HTTP/")) {
            protocol = protocol.substring("HTTP/".length());
        }
        // Some examples of semantic attributes as defined here:
        // https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/semantic_conventions/http-metrics.md#attributes
        // Note that this might be incomplete.
        return Attributes.builder()
                /* commented out for simpler metrics during the presentation
                .put(SemanticAttributes.HTTP_METHOD, request.getMethod())
                .put(SemanticAttributes.HTTP_HOST, request.getRemoteHost())
                .put(SemanticAttributes.HTTP_SCHEME, request.getScheme())
                .put(SemanticAttributes.HTTP_FLAVOR, protocol)
                .put(SemanticAttributes.HTTP_SERVER_NAME, request.getServerName())
                .put(SemanticAttributes.HTTP_URL, "/")
                .put(SemanticAttributes.HTTP_TARGET, "/")
                 */
                .build();
    }

    private Attributes extractAttributes(HttpServletRequest request, HttpServletResponse response) {
        return Attributes.builder()
                .putAll(extractAttributes(request))
                .put(SemanticAttributes.HTTP_STATUS_CODE, response.getStatus())
                .build();
    }

    public GreeterServlet(Meter meter) {
        activeRequests = meter.upDownCounterBuilder("http.server.active_requests")
                .setUnit("requests")
                .setDescription("The number of concurrent HTTP requests that are currently in-flight")
                .build();

        duration = meter.histogramBuilder("http.server.duration")
                .setUnit("milliseconds")
                .setDescription("The duration of the inbound HTTP request")
                .build();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.nanoTime();
        activeRequests.add(1, extractAttributes(request));
        String name = request.getPathInfo().substring(1); // strip leading slash
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            response.setStatus(parseInt(status));
            response.getWriter().print(greeting);
        } catch (GreetingNotFoundException e) {
            status = "500";
            response.setStatus(parseInt(status));
        } finally {
            double durationMillis = (System.nanoTime() - start) * 1e-6;
            activeRequests.add(-1, extractAttributes(request));
            duration.record(durationMillis, extractAttributes(request, response));
        }
    }
}
