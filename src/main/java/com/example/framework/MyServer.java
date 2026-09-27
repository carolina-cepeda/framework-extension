package com.example.framework;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyServer {

    private static final int DEFAULT_PORT = 6000;
    private static final int THREAD_POOL_SIZE = 10;

    private final HttpServer server;
    private final ThreadPoolExecutor executor;

    public MyServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        server.createContext("/greeting", new GreetingHandler());
        server.setExecutor(executor);
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + server.getAddress().getPort());
        System.out.println("Endpoint: http://localhost:" + server.getAddress().getPort() + "/greeting?name=World");
    }

    public void stop(int delaySeconds) {
        System.out.println("Shutting down server...");
        server.stop(delaySeconds);
        executor.shutdown();
        System.out.println("Server stopped");
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", String.valueOf(DEFAULT_PORT)));

        try {
            MyServer myServer = new MyServer(port);
            myServer.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                myServer.stop(5);
            }));

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }
}