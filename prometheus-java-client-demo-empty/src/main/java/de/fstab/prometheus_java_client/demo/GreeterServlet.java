package de.fstab.prometheus_java_client.demo;

import de.fstab.greeter.Greeter;
import de.fstab.greeter.GreetingNotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Integer.parseInt;

public class GreeterServlet extends HttpServlet {

    private final Greeter greeter = new Greeter();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getPathInfo().substring(1); // strip leading slash
        String status = "200";
        try {
            String greeting = greeter.sayHello(name);
            response.setStatus(parseInt(status));
            response.getWriter().print(greeting);
        } catch (GreetingNotFoundException e) {
            status = "500";
            response.setStatus(parseInt(status));
        }
    }
}