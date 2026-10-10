package dev.gxstein.expensetracker.service.exception;

public class InvalidExpenseFilterException extends RuntimeException {
    public InvalidExpenseFilterException(String message) {
        super(message);
    }
}
