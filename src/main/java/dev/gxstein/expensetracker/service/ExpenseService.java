package dev.gxstein.expensetracker.service;

import dev.gxstein.expensetracker.api.dto.ExpenseRequest;
import dev.gxstein.expensetracker.api.dto.ExpenseResponse;
import dev.gxstein.expensetracker.api.dto.MonthlySummaryResponse;
import dev.gxstein.expensetracker.domain.Expense;
import dev.gxstein.expensetracker.domain.User;
import dev.gxstein.expensetracker.repository.ExpenseRepository;
import dev.gxstein.expensetracker.repository.UserRepository;
import dev.gxstein.expensetracker.service.exception.InvalidExpenseFilterException;
import dev.gxstein.expensetracker.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenses;
    private final UserRepository users;

    public ExpenseService(ExpenseRepository expenses, UserRepository users) {
        this.expenses = expenses;
        this.users = users;
    }

    @Transactional
    public ExpenseResponse create(Long ownerId, ExpenseRequest request) {
        User owner = users.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));
        Expense expense = new Expense(
                request.description().strip(),
                request.amount(),
                request.date(),
                request.category(),
                owner);
        return ExpenseResponse.from(expenses.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(Long ownerId, Integer month, Integer year) {
        if (month == null && year == null) {
            return expenses.findAllByOwnerIdOrderByDateDescIdDesc(ownerId)
                    .stream().map(ExpenseResponse::from).toList();
        }
        YearMonth period = parsePeriod(month, year);
        return expenses.findAllByOwnerIdAndDateBetweenOrderByDateDescIdDesc(
                        ownerId, period.atDay(1), period.atEndOfMonth())
                .stream().map(ExpenseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long ownerId, Long expenseId) {
        return ExpenseResponse.from(findOwnedExpense(ownerId, expenseId));
    }

    @Transactional
    public ExpenseResponse update(Long ownerId, Long expenseId, ExpenseRequest request) {
        Expense expense = findOwnedExpense(ownerId, expenseId);
        expense.update(
                request.description().strip(),
                request.amount(),
                request.date(),
                request.category());
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void delete(Long ownerId, Long expenseId) {
        expenses.delete(findOwnedExpense(ownerId, expenseId));
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse summarize(Long ownerId, Integer month, Integer year) {
        YearMonth period = parsePeriod(month, year);
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();
        BigDecimal total = expenses.sumAmountByOwnerAndDateRange(ownerId, start, end);
        long count = expenses.countByOwnerIdAndDateBetween(ownerId, start, end);
        return new MonthlySummaryResponse(period.toString(), count, total);
    }

    private Expense findOwnedExpense(Long ownerId, Long expenseId) {
        return expenses.findByIdAndOwnerId(expenseId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found."));
    }

    private YearMonth parsePeriod(Integer month, Integer year) {
        if (month == null || year == null) {
            throw new InvalidExpenseFilterException("Both month and year are required.");
        }
        try {
            return YearMonth.of(year, month);
        } catch (DateTimeException exception) {
            throw new InvalidExpenseFilterException("Month must be 1-12 and year must be valid.");
        }
    }
}
