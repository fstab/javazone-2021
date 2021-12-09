package com.example.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class GreeterResourceWithAnnotations {

    private final Greeter greeter = new Greeter();

    @GetMapping("/hello-with-annotations/{name}")
    @Timed(
            value = "greeter.request.duration.with.annotation",
            percentiles = { 0.5, 0.95, 0.99 },
            histogram = true
    )
    public String hello(@PathVariable("name") String name, HttpServletResponse response) {
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            response.setStatus(Integer.parseInt(status));
            return greeting;
        } catch (GreetingNotFoundException e) {
            status = "500";
            response.setStatus(Integer.parseInt(status));
            return "";
        }
    }
}
