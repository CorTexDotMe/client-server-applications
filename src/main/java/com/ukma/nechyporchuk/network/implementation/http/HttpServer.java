package com.ukma.nechyporchuk.network.implementation.http;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

public class HttpServer {
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
                new EndpointHandler("api/good/?", "GET", this::getAllProducts)
        );

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder builder = new StringBuilder();

            builder.append("<h1>URI: ").append(exchange.getRequestURI()).append("</h1>");

            Headers headers = exchange.getRequestHeaders();
            for (String header : headers.keySet()) {
                builder.append("<p>").append(header).append("=")
                        .append(headers.getFirst(header)).append("</p>");
            }

            byte[] bytes = builder.toString().getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private void process(HttpExchange exchange, Object json) {

        }

        private void getAllProducts(HttpExchange exchange) {

//            process(exchange, );
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
            exchange.getRequestHeaders().put("content-type", List.of("application/json"));
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
