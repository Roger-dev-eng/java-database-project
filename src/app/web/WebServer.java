package app.web;

import app.db.Database;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dml.Delete;
import dml.Insert;
import dml.Update;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WebServer {
    private static final Gson JSON = new Gson();
    private static final Path WEB_ROOT = Path.of("frontend");

    private WebServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", WebServer::handle);
        server.start();
        System.out.println("Site Java ativo na porta " + port);
    }

    private static void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/api/")) {
                handleApi(exchange, path.substring(5));
                return;
            }
            serveStatic(exchange, path);
        } catch (Throwable exception) {
            Throwable cause = exception.getCause() == null ? exception : exception.getCause();
            sendJson(exchange, 500, Map.of("error", cause.getMessage() == null ? "Erro interno no backend Java." : cause.getMessage()));
        }
    }

    private static void serveStatic(HttpExchange exchange, String requestPath) throws IOException {
        String relative = "/".equals(requestPath) ? "/index.html" : requestPath;
        Path file = WEB_ROOT.resolve(relative.substring(1)).normalize();
        if (!file.startsWith(WEB_ROOT) || !Files.exists(file) || Files.isDirectory(file)) {
            sendText(exchange, 404, "Página não encontrada.", "text/plain; charset=UTF-8");
            return;
        }
        String contentType = relative.endsWith(".css") ? "text/css; charset=UTF-8" : relative.endsWith(".js") ? "application/javascript; charset=UTF-8" : "text/html; charset=UTF-8";
        sendText(exchange, 200, Files.readString(file), contentType);
    }

    private static void handleApi(HttpExchange exchange, String resourcePath) throws Exception {
        String[] parts = resourcePath.split("/");
        String resource = parts[0];
        if ("dashboard".equals(resource) && "GET".equals(exchange.getRequestMethod())) {
            sendJson(exchange, 200, dashboard());
            return;
        }
        Resource definition = Resource.from(resource);
        if (definition == null) {
            sendJson(exchange, 404, Map.of("error", "Recurso não encontrado."));
            return;
        }
        Integer id = parts.length > 1 ? Integer.valueOf(parts[1]) : null;
        try (Connection connection = Database.conectar()) {
            if ("GET".equals(exchange.getRequestMethod())) sendJson(exchange, 200, query(connection, definition.select));
            else if ("POST".equals(exchange.getRequestMethod())) { insert(connection, definition, readJson(exchange)); sendJson(exchange, 201, Map.of("ok", true)); }
            else if ("PUT".equals(exchange.getRequestMethod()) && id != null) { update(connection, definition, id, readJson(exchange)); sendJson(exchange, 200, Map.of("ok", true)); }
            else if ("DELETE".equals(exchange.getRequestMethod()) && id != null) { delete(connection, definition, id); sendJson(exchange, 200, Map.of("ok", true)); }
            else sendJson(exchange, 405, Map.of("error", "Método não permitido."));
        }
    }

    private static void insert(Connection connection, Resource resource, JsonObject data) throws SQLException {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < resource.columns.length; i++) placeholders.append(i == 0 ? "?" : ", ?");
        String sql = "INSERT INTO " + resource.table + " (" + String.join(", ", resource.columns) + ") VALUES (" + placeholders + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) { bind(statement, resource.columns, data); statement.executeUpdate(); }
    }

    private static void update(Connection connection, Resource resource, int id, JsonObject data) throws SQLException {
        StringBuilder assignments = new StringBuilder();
        for (String column : resource.columns) assignments.append(assignments.length() == 0 ? "" : ", ").append(column).append("=?");
        try (PreparedStatement statement = connection.prepareStatement("UPDATE " + resource.table + " SET " + assignments + " WHERE " + resource.id + "=?")) {
            int position = bind(statement, resource.columns, data);
            statement.setInt(position, id);
            if (statement.executeUpdate() == 0) throw new SQLException("Registro não encontrado.");
        }
    }

    private static void delete(Connection connection, Resource resource, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + resource.table + " WHERE " + resource.id + "=?")) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) throw new SQLException("Registro não encontrado.");
        }
    }

    private static int bind(PreparedStatement statement, String[] columns, JsonObject data) throws SQLException {
        int position = 1;
        for (String column : columns) {
            if (!data.has(column) || data.get(column).isJsonNull() || data.get(column).getAsString().isBlank()) {
                statement.setNull(position++, sqlType(column));
                continue;
            }
            String value = data.get(column).getAsString();
            if (isIntegerColumn(column)) statement.setInt(position++, Integer.parseInt(value));
            else if ("data_avaliacao".equals(column)) statement.setDate(position++, java.sql.Date.valueOf(value));
            else statement.setString(position++, value);
        }
        return position;
    }

    private static boolean isIntegerColumn(String column) {
        return "ano_lancamento".equals(column)
                || "horas_jogadas".equals(column)
                || "nota".equals(column)
                || "fk_jogo".equals(column)
                || "fk_jogador".equals(column);
    }

    private static int sqlType(String column) {
        if (isIntegerColumn(column)) return Types.INTEGER;
        if ("data_avaliacao".equals(column)) return Types.DATE;
        return Types.VARCHAR;
    }

    private static List<Map<String, Object>> query(Connection connection, String sql) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            ResultSetMetaData metadata = result.getMetaData();
            while (result.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int index = 1; index <= metadata.getColumnCount(); index++) {
                    Object value = result.getObject(index);
                    if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp) value = value.toString();
                    row.put(metadata.getColumnLabel(index), value);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private static Map<String, Object> dashboard() throws SQLException {
        try (Connection connection = Database.conectar()) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("jogos", query(connection, "SELECT COUNT(*) AS total FROM jogos"));
            data.put("jogadores", query(connection, "SELECT COUNT(*) AS total FROM jogadores"));
            data.put("media", query(connection, "SELECT COALESCE(ROUND(AVG(nota), 2), 0) AS total FROM avaliacoes"));
            data.put("generos", query(connection, "SELECT COALESCE(genero, 'Sem gênero') AS nome, COUNT(*) AS total FROM jogos GROUP BY genero ORDER BY total DESC"));
            data.put("ranking", query(connection, "SELECT j.nome, COUNT(a.id_avaliacao) AS total FROM jogos j LEFT JOIN avaliacoes a ON a.fk_jogo=j.id_jogo GROUP BY j.id_jogo, j.nome ORDER BY total DESC LIMIT 10"));
            return data;
        }
    }

    private static JsonObject readJson(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) { return JsonParser.parseString(new String(input.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject(); }
    }

    private static void sendJson(HttpExchange exchange, int status, Object value) throws IOException { sendText(exchange, status, JSON.toJson(value), "application/json; charset=UTF-8"); }

    private static void sendText(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) { output.write(bytes); }
    }

    private record Resource(String table, String id, String[] columns, String select) {
        private static Resource from(String name) {
            return switch (name) {
                case "jogos" -> new Resource("jogos", "id_jogo", new String[]{"nome", "ano_lancamento", "desenvolvedora", "genero"}, "SELECT id_jogo, nome, ano_lancamento, desenvolvedora, genero FROM jogos ORDER BY id_jogo");
                case "jogadores" -> new Resource("jogadores", "id_jogador", new String[]{"nickname", "email", "fk_jogo"}, "SELECT id_jogador, nickname, email, fk_jogo FROM jogadores ORDER BY id_jogador");
                case "plataformas" -> new Resource("plataformas", "id_plataforma", new String[]{"nome", "horas_jogadas", "fk_jogador"}, "SELECT id_plataforma, nome, horas_jogadas, ultima_sessao, fk_jogador FROM plataformas ORDER BY id_plataforma");
                case "avaliacoes" -> new Resource("avaliacoes", "id_avaliacao", new String[]{"nota", "comentario", "status", "data_avaliacao", "fk_jogador", "fk_jogo"}, "SELECT id_avaliacao, nota, comentario, status, data_avaliacao, fk_jogador, fk_jogo FROM avaliacoes ORDER BY id_avaliacao");
                default -> null;
            };
        }
    }
}