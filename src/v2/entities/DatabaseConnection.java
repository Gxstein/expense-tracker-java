package v2.entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnection() {
        this(
                environmentOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/expense_tracker"),
                environmentOrDefault("DB_USERNAME", "expense_tracker"),
                environmentOrDefault("DB_PASSWORD", "expense_tracker"));
    }

    DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
