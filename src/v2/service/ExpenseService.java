package v2.service;

import v2.entities.Expense;
import v2.repository.ExpenseDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExpenseService {
    private final ExpenseDAO dao;

    public ExpenseService(ExpenseDAO dao) {
        this.dao = dao;
    }

    public Expense register(Expense expense) throws SQLException {
        return dao.save(expense);
    }

    public List<Expense> listAll() throws SQLException {
        return dao.findAll();
    }

    public List<Expense> listByMonth(int month, int year) throws SQLException {
        return dao.findByMonth(month, year);
    }

    public BigDecimal calculateTotal() throws SQLException {
        return dao.calculateTotal();
    }

    public boolean update(Expense expense) throws SQLException {
        return dao.update(expense);
    }

    public boolean delete(Long id) throws SQLException {
        return dao.delete(id);
    }

    public Expense findById(Long id) throws SQLException {
        return dao.findById(id).orElse(null);
    }
}
