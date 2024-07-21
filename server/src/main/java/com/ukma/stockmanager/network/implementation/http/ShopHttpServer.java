package com.ukma.stockmanager.network.implementation.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import com.ukma.stockmanager.core.entities.Item;
import com.ukma.stockmanager.core.security.JWT;
import com.ukma.stockmanager.database.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShopHttpServer {
    public static final Database DATABASE = new Database("Shop database");
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
                new EndpointHandler("/api/groups/?", "GET", this::getAllGroups),
                new EndpointHandler("/api/goods/?", "GET", this::getAllItems),
                new EndpointHandler("/api/good/(\\d+)", "GET", this::getItem),
                new EndpointHandler("/api/good", "PUT", this::putItem),
                new EndpointHandler("/api/good/(\\d+)", "POST", this::updateItem),
                new EndpointHandler("/api/good/(\\d+)", "DELETE", this::deleteItem),

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

        private void getAllItems(HttpExchange exchange) {
            process(exchange, DATABASE.readAllItems(), 200);
        }

        private void getItem(HttpExchange exchange) {
            String id = exchange.getRequestURI().getPath().replace("/api/good/", "");
            Item item = DATABASE.readItem(Integer.parseInt(id));

            if (item != null)
                process(exchange, item, 200);
            else
                process(exchange, "Error", 404);
        }

        private void putItem(HttpExchange exchange) {
            try {
                InputStream in = exchange.getRequestBody();
                Item item = OBJECT_MAPPER.readValue(in, Item.class);

                if (!DATABASE.createItem(item))
                    throw new IOException();

                int id = DATABASE.readItemId(item.getName());
                process(exchange, Map.of("id", id), 201);
            } catch (IOException e) {
                process(exchange, "Conflict", 409);
            }
        }

        private void updateItem(HttpExchange exchange) {
            try {
                String id = exchange.getRequestURI().getPath().replace("/api/good/", "");
                Item item = DATABASE.readItem(Integer.parseInt(id));
                if (item == null)
                    throw new IllegalArgumentException();

                InputStream in = exchange.getRequestBody();
                Map<String, Object> map = OBJECT_MAPPER.readValue(in, new TypeReference<>() {
                });

                for (var entry : map.entrySet()) {
                    switch (entry.getKey()) {
                        case "name":
                            DATABASE.updateItemName(item.getId(), (String) entry.getValue());
                            break;
                        case "description":
                            DATABASE.updateItemDescription(item.getId(), (String) entry.getValue());
                            break;
                        case "amount":
                            DATABASE.updateItemAmount(item.getId(), (int) entry.getValue());
                            break;
                        case "cost":
                            DATABASE.updateItemCost(item.getId(), (double) entry.getValue());
                            break;
                        case "producer":
                            DATABASE.updateItemProducer(item.getId(), (String) entry.getValue());
                            break;
                        case "group":
                            DATABASE.updateItemGroup(item.getId(), (String) entry.getValue());
                            break;
                    }
                }

                process(exchange, null, 204);
            } catch (IOException | ClassCastException e) {
                process(exchange, "Conflict", 409);
            } catch (IllegalArgumentException e) {
                process(exchange, "Error", 404);
            }
        }

        public void deleteItem(HttpExchange exchange) {
            String id = exchange.getRequestURI().getPath().replace("/api/good/", "");
            boolean success = DATABASE.deleteItem(Integer.parseInt(id));

            if (success)
                process(exchange, null, 204);
            else
                process(exchange, "Error", 404);
        }

        private void processLogin(HttpExchange exchange) {
            try {
                String query = exchange.getRequestURI().getRawQuery();
                Map<String, String> parameters = queryToMap(query);

                String loginMDHash = parameters.get("login");
                if (loginMDHash == null)
                    throw new IllegalArgumentException();

                String passwordMDHash = parameters.get("password");
                if (passwordMDHash == null || !DATABASE.containsUser(loginMDHash, passwordMDHash))
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
        private final String pathPattern;
        private final String method;
        private final Consumer<HttpExchange> handler;

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
            if (DATABASE.containsLogin(login))
                return new Success(new HttpPrincipal("c0nst", "realm"));
            else
                return new Failure(403);

        }
    }
}
