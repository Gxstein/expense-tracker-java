package v2.entities;

import v2.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private Category category;

    public Expense(Long id, String description, BigDecimal amount, LocalDate date, Category category) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public Expense(String description, BigDecimal amount, LocalDate date, Category category) {
        this(null, description, amount, date, category);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "#%d | %s | %s | %s | %s".formatted(id, date, description, amount, category);
    }
}
