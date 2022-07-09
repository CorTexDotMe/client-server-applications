package com.ukma.nechyporchuk.network.implementation.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import com.ukma.nechyporchuk.database.Database;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

public class HttpServer {
    public static final Database DATABASE = new Database("Shop database");
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create();
        server.bind(new InetSocketAddress(8765), 0);

        HttpContext context = server.createContext("/", new EchoHandler());
        context.setAuthenticator(new Auth());

        server.setExecutor(null);
        server.start();
    }

    static class EchoHandler implements HttpHandler {
        private final List<EndpointHandler> handlers = List.of(
                new EndpointHandler("/api/good/?", "GET", this::getAllGroups)
        );

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            handlers.stream()
                    .filter(endpointHandler -> endpointHandler.isMatch(exchange))
                    .findFirst()
                    .ifPresentOrElse(
                            endpointHandler -> endpointHandler.handle(exchange),
                            processUnknownEndpoint(exchange)
                    );


//            StringBuilder builder = new StringBuilder();
//
//            builder.append("<h1>URI: ").append(exchange.getRequestURI()).append("</h1>");
//
//            Headers headers = exchange.getRequestHeaders();
//            for (String header : headers.keySet()) {
//                builder.append("<p>").append(header).append("=")
//                        .append(headers.getFirst(header)).append("</p>");
//            }
//
//            byte[] bytes = builder.toString().getBytes();
//            exchange.sendResponseHeaders(200, bytes.length);
//
//            OutputStream os = exchange.getResponseBody();
//            os.write(bytes);
//            os.close();
        }

        private Runnable processUnknownEndpoint(HttpExchange exchange) {
            return () -> {
                try {
                    byte[] response = "Error".getBytes();
                    exchange.sendResponseHeaders(404, response.length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }

        private void process(HttpExchange exchange, Object data, int code) {
            try {
                byte[] jsonInBytes = OBJECT_MAPPER.writeValueAsBytes(data);
                exchange.sendResponseHeaders(code, jsonInBytes.length);

                OutputStream os = exchange.getResponseBody();
                os.write(jsonInBytes);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getAllGroups(HttpExchange exchange) {
            process(exchange, DATABASE.readAllGroups(), 200);
        }
    }

    static class EndpointHandler {
        private String pathPattern;
        private String method;
        private Consumer<HttpExchange> handler;

        public EndpointHandler(String pathPattern, String method, Consumer<HttpExchange> handler) {
            this.pathPattern = pathPattern;
            this.method = method;
            this.handler = handler;
        }

        public boolean isMatch(HttpExchange exchange) {
            if (!exchange.getRequestMethod().equals(this.method)) return false;

            String path = exchange.getRequestURI().getPath();
            return path.matches(this.pathPattern);
        }

        public void handle(HttpExchange exchange) {
            exchange.getResponseHeaders().add("content-type","application/json");
            handler.accept(exchange);
        }

    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}
