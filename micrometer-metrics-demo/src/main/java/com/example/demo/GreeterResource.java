package com.example.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


@RestController
public class GreeterResource {

    private final Greeter greeter = new Greeter();

    public GreeterResource(MeterRegistry meterRegistry) {
        meterRegistry.config().meterFilter(new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getName().equals("greeter.request.duration")) {
                    return DistributionStatisticConfig.builder()
                            .percentiles(0.5, 0.95, 0.99)
                            .percentilePrecision(1)
                            .percentilesHistogram(true)
                            .serviceLevelObjectives(MILLISECONDS.toNanos(120))
                            .build()
                            .merge(config);
                }
                return config;
            }
        });
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name, HttpServletResponse response) {
        long start = System.nanoTime();
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            response.setStatus(Integer.parseInt(status));
            return greeting;
        } catch (GreetingNotFoundException e) {
            status = "500";
            response.setStatus(Integer.parseInt(status));
            return "";
        } finally {
            Metrics.counter("greeter.request.count", Tags.of("status", status)).increment();
            Metrics.timer("greeter.request.duration", Tags.of("status", status))
                    .record(Duration.ofNanos(System.nanoTime() - start));
        }
    }
}
