package de.fstab.prometheus_java_client.demo;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.filter.MetricsFilter;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;

public class DemoApplication {

    public static void main(String[] args) throws Exception {
        DefaultExports.initialize();
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8081);
        server.setConnectors(new Connector[]{connector});
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(GreeterServlet.class, "/hello/*");

        // Expose Prometheus metrics on /metrics
        servletHandler.addServletWithMapping(MetricsServlet.class, "/metrics");

        // Example of automatic instrumentation via Servlet filter.
        double[] buckets = new double[]{0.0125, 0.025, 0.0375, 0.05, 0.0625, 0.075, 0.0875, 0.10, 0.1125, 0.125, 0.1375, 0.15, 0.1625, 0.175, 0.1875, 0.20, 0.2125, 0.225, 0.2375, 0.25, 0.2625, 0.275, 0.2875, 0.30};
        FilterHolder filterHolder = new FilterHolder(new MetricsFilter("greeter_request_duration_with_servlet_filter", "Greeter request duration", null, buckets));
        servletHandler.addFilter(filterHolder);

        server.setHandler(servletHandler);
        server.start();
    }
}
