package de.fstab.dropwizard.demo;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import org.eclipse.jetty.server.HttpConfiguration;

public class DemoApplication extends Application<DemoApplicationConfig> {

    private MetricRegistry registry;

    public static void main(final String[] args) throws Exception {
        new DemoApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<DemoApplicationConfig> bootstrap) {
        this.registry = bootstrap.getMetricRegistry();
    }

    @Override
    public void run(final DemoApplicationConfig configuration,
                    final Environment environment) {
        GreeterResource greeterResource = new GreeterResource(registry);
        environment.jersey().register(greeterResource);
        GreeterResourceWithAnnotations greeterResourceWithAnnotations = new GreeterResourceWithAnnotations();
        environment.jersey().register(greeterResourceWithAnnotations);
        // Export metrics in Dropwizard's JSON format on /metrics
        environment.servlets().addServlet("DropwizardMetricsServlet", new MetricsServlet(registry))
                .addMapping("/metrics");
        // Export metrics converted to Prometheus format on /prometheus-metrics
        CollectorRegistry.defaultRegistry.register(new DropwizardExports(registry));
        environment.servlets().addServlet("PrometheusMetricsServlet", new io.prometheus.client.exporter.MetricsServlet())
                .addMapping("/prometheus-metrics");
    }
}
