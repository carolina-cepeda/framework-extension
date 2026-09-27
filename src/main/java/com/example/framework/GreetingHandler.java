package com.example.framework;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class GreetingHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String name = "World";

        if (query != null) {
            Map<String, String> params = java.util.Arrays.stream(query.split("&"))
                    .map(pair -> pair.split("=", 2))
                    .collect(toMap(
                            pair -> pair[0],
                            pair -> pair.length > 1 ? pair[1] : ""
                    ));
            name = params.getOrDefault("name", "World");
        }

        String response = "Hello, " + name + "!";
        sendResponse(exchange, 200, response);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}