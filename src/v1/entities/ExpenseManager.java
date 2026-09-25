package v1.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores expenses in memory and provides operations over them.
 */
public class ExpenseManager {

    private List<Expense> expenses = new ArrayList<>();

    /**
     * Adds a new expense to the list.
     */
    public void add(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Removes the expense with the given id, if it exists.
     */
    public void remove(Long id) {
        for (Expense e : expenses) {
            if (e.getId().equals(id)) {
                expenses.remove(e);
                // Stop the loop after removing to avoid ConcurrentModificationException
                break;
            }
        }
    }

    /**
     * Returns all registered expenses.
     */
    public List<Expense> listAll() {
        return expenses;
    }

    /**
     * Returns only the expenses from the given month and year.
     */
    public List<Expense> filterByMonth(int month, int year) {
        List<Expense> expenseMonthYear = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getDate().getMonthValue() == month && e.getDate().getYear() == year) {
                expenseMonthYear.add(e);
            }
        }
        return expenseMonthYear;
    }

    /**
     * Returns the sum of all expense amounts.
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense e : expenses) {
            total = total.add(e.getAmount());
        }
        return total;
    }
}