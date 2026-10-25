package v2.repository;

import v2.entities.DatabaseConnection;
import v2.entities.Expense;
import v2.enums.Category;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExpenseDAOJdbc implements ExpenseDAO {
    private final DatabaseConnection database;

    public ExpenseDAOJdbc(DatabaseConnection database) {
        this.database = database;
    }

    @Override
    public Expense save(Expense expense) throws SQLException {
        String sql = """
                INSERT INTO expense_v2 (description, amount, expense_date, category)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindExpense(statement, expense);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setId(keys.getLong(1));
                }
            }
        }
        return expense;
    }

    @Override
    public Optional<Expense> findById(Long id) throws SQLException {
        String sql = "SELECT id, description, amount, expense_date, category FROM expense_v2 WHERE id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(map(results)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Expense> findAll() throws SQLException {
        String sql = """
                SELECT id, description, amount, expense_date, category
                FROM expense_v2
                ORDER BY expense_date DESC, id DESC
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            List<Expense> expenses = new ArrayList<>();
            while (results.next()) {
                expenses.add(map(results));
            }
            return expenses;
        }
    }

    @Override
    public List<Expense> findByMonth(int month, int year) throws SQLException {
        YearMonth period = YearMonth.of(year, month);
        String sql = """
                SELECT id, description, amount, expense_date, category
                FROM expense_v2
                WHERE expense_date BETWEEN ? AND ?
                ORDER BY expense_date DESC, id DESC
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(period.atDay(1)));
            statement.setDate(2, Date.valueOf(period.atEndOfMonth()));
            try (ResultSet results = statement.executeQuery()) {
                List<Expense> expenses = new ArrayList<>();
                while (results.next()) {
                    expenses.add(map(results));
                }
                return expenses;
            }
        }
    }

    @Override
    public BigDecimal calculateTotal() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expense_v2";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            results.next();
            return results.getBigDecimal("total");
        }
    }

    @Override
    public boolean update(Expense expense) throws SQLException {
        String sql = """
                UPDATE expense_v2
                SET description = ?, amount = ?, expense_date = ?, category = ?
                WHERE id = ?
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindExpense(statement, expense);
            statement.setLong(5, expense.getId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM expense_v2 WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void bindExpense(PreparedStatement statement, Expense expense) throws SQLException {
        statement.setString(1, expense.getDescription());
        statement.setBigDecimal(2, expense.getAmount());
        statement.setDate(3, Date.valueOf(expense.getDate()));
        statement.setString(4, expense.getCategory().name());
    }

    private Expense map(ResultSet results) throws SQLException {
        LocalDate date = results.getDate("expense_date").toLocalDate();
        return new Expense(
                results.getLong("id"),
                results.getString("description"),
                results.getBigDecimal("amount"),
                date,
                Category.valueOf(results.getString("category")));
    }
}
