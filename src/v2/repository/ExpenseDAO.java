package v2.repository;

import v2.entities.Expense;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExpenseDAO {
    Expense save(Expense expense) throws SQLException;

    Optional<Expense> findById(Long id) throws SQLException;

    List<Expense> findAll() throws SQLException;

    List<Expense> findByMonth(int month, int year) throws SQLException;

    BigDecimal calculateTotal() throws SQLException;

    boolean update(Expense expense) throws SQLException;

    boolean delete(Long id) throws SQLException;
}
