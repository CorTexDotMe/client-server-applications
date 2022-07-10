package com.ukma.nechyporchuk.network.implementation.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import com.ukma.nechyporchuk.database.Database;
import com.ukma.nechyporchuk.security.JWT;
import com.ukma.nechyporchuk.security.PacketCipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShopHttpServer {
    public static final Database DATABASE = new Database("Shop database");
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final MessageDigest MD;

    static {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            md = null;
        }
        MD = md;
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8765), 0);

        HttpContext context = server.createContext("/", new EchoHandler());
        context.setAuthenticator(new Auth());

        server.setExecutor(null);
        server.start();
    }

    static class EchoHandler implements HttpHandler {
        private final List<EndpointHandler> handlers = List.of(
                new EndpointHandler("/api/good/?", "GET", this::getAllGroups),


                new EndpointHandler("/login", "POST", this::processLogin)
        );

        @Override
        public void handle(HttpExchange exchange) {
            handlers.stream()
                    .filter(endpointHandler -> endpointHandler.isMatch(exchange))
                    .findFirst()
                    .ifPresentOrElse(
                            endpointHandler -> endpointHandler.handle(exchange),
                            processUnknownEndpoint(exchange)
                    );
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

        private void processLogin(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getRawQuery();
                Map<String, String> parameters = queryToMap(query);

                String loginMDHash = parameters.get("login");
                if (loginMDHash == null || !loginMDHash.equals("1WtpmDDne6U4VWecsdJS2g=="))
                    throw new IllegalArgumentException();

                String passwordMDHash = parameters.get("password");
                if (passwordMDHash == null || !passwordMDHash.equals("X03MO1qnZdYdgyfeuILPmQ=="))
                    throw new IllegalArgumentException();

                String token = JWT.createJWT(loginMDHash);
                process(exchange, Map.of("jwt", token), 200);
            } catch (IllegalArgumentException e) {
                process(exchange, "Error", 401);
            }
        }

        private static Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
            return result;
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
            exchange.getResponseHeaders().add("content-type", "application/json");
            handler.accept(exchange);
        }

    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange exchange) {

            if (exchange.getRequestURI().getPath().equals("/login"))
                return new Success(new HttpPrincipal("c0nst", "realm"));

            String jwt = exchange.getRequestHeaders().getFirst("jwt");
            if (jwt == null)
                return new Failure(403);

            String login = JWT.parseJWT(jwt);
            if (login.equals("1WtpmDDne6U4VWecsdJS2g=="))//user exist TODO processor for amount change
                return new Success(new HttpPrincipal("c0nst", "realm"));
            else
                return new Failure(403);

        }
    }
}
