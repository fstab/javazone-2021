package de.fstab.metrics.demo;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final List<String> names = List.of(
            // The following are copied from Database.java
            "liam",
            "olivia",
            "noah",
            "emma",
            "oliver",
            "ava",
            "elijah",
            "charlotte",
            "william",
            "sophia",
            "james",
            "amelia",
            "benjamin",
            "isabella",
            "lucas",
            "mia",
            "henry",
            "evelyn",
            "alexander",
            "harper",
            // The following are not in the database and will result in HTTP 500 errors
            "mason",
            "camila",
            "michael",
            "gianna"
    );

    private static final String BASE_URL = "http://localhost:8080/hello/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(names.size());

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar client.jar <port>");
            System.exit(-1);
        }
        String baseUrl = "http://localhost:" + args[0] + "/hello/";
        for (int i = 0; i < names.size(); i++) {
            final String name = names.get(i);
            final long delayMillis = i * (2000L / names.size());
            executorService.scheduleAtFixedRate(() -> runGetRequest(baseUrl + name), delayMillis, 2000, TimeUnit.MILLISECONDS);
        }
    }

    private static void runGetRequest(String url) {
        StringBuilder msg = new StringBuilder();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        msg.append("GET ").append(url).append(" -> ");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                msg.append("HTTP ").append(response.statusCode());
            } else {
                msg.append(response.body().trim());
            }
        } catch (ConnectException e) {
            msg.append("connection refused");
        } catch (Exception e) {
            msg.append(e.getMessage());
        }
        System.out.println(msg);
    }
}
