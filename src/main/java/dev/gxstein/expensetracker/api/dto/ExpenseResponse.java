package dev.gxstein.expensetracker.api.dto;

import dev.gxstein.expensetracker.domain.Category;
import dev.gxstein.expensetracker.domain.Expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate date,
        Category category,
        Instant createdAt) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory(),
                expense.getCreatedAt());
    }
}
