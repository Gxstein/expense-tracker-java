package v2.application;

import v2.entities.DatabaseConnection;
import v2.entities.Expense;
import v2.enums.Category;
import v2.repository.ExpenseDAOJdbc;
import v2.service.ExpenseService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Program {
    private final Scanner scanner;
    private final ExpenseService service;

    private Program(Scanner scanner, ExpenseService service) {
        this.scanner = scanner;
        this.service = service;
    }

    public static void main(String[] args) {
        ExpenseService service = new ExpenseService(new ExpenseDAOJdbc(new DatabaseConnection()));
        try (Scanner scanner = new Scanner(System.in)) {
            new Program(scanner, service).run();
        } catch (SQLException exception) {
            System.err.println("Database operation failed. Check the PostgreSQL connection and credentials.");
        }
    }

    private void run() throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n-- Expense Tracker (JDBC) --");
            System.out.println("1 - Add expense");
            System.out.println("2 - List all expenses");
            System.out.println("3 - List expenses by month");
            System.out.println("4 - Update expense");
            System.out.println("5 - Delete expense");
            System.out.println("6 - Show total");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().strip();
            try {
                switch (option) {
                    case "1" -> addExpense();
                    case "2" -> printExpenses(service.listAll());
                    case "3" -> listByMonth();
                    case "4" -> updateExpense();
                    case "5" -> deleteExpense();
                    case "6" -> System.out.println("Total: " + service.calculateTotal());
                    case "0" -> running = false;
                    default -> System.out.println("Choose a number from 0 to 6.");
                }
            } catch (IllegalArgumentException | DateTimeException exception) {
                System.out.println("Invalid input: " + exception.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private void addExpense() throws SQLException {
        Expense saved = service.register(readExpense(null));
        System.out.println("Expense added with id " + saved.getId() + ".");
    }

    private void listByMonth() throws SQLException {
        System.out.print("Month (1-12): ");
        int month = Integer.parseInt(scanner.nextLine().strip());
        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine().strip());
        YearMonth.of(year, month);
        printExpenses(service.listByMonth(month, year));
    }

    private void updateExpense() throws SQLException {
        System.out.print("Expense id: ");
        long id = Long.parseLong(scanner.nextLine().strip());
        Expense existing = service.findById(id);
        if (existing == null) {
            System.out.println("Expense not found.");
            return;
        }
        Expense updated = readExpense(id);
        if (service.update(updated)) {
            System.out.println("Expense updated.");
        } else {
            System.out.println("Expense not found.");
        }
    }

    private void deleteExpense() throws SQLException {
        System.out.print("Expense id: ");
        long id = Long.parseLong(scanner.nextLine().strip());
        System.out.println(service.delete(id) ? "Expense deleted." : "Expense not found.");
    }

    private Expense readExpense(Long id) {
        System.out.print("Description: ");
        String description = scanner.nextLine().strip();
        if (description.isBlank() || description.length() > 255) {
            throw new IllegalArgumentException("Description must contain 1-255 characters.");
        }

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().strip());
        if (amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must be positive and have at most 2 decimal places.");
        }
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);

        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().strip());
        System.out.print("Category (FOOD, TRANSPORT, HOUSING, LEISURE, HEALTH, OTHER): ");
        Category category = Category.valueOf(scanner.nextLine().strip().toUpperCase(Locale.ROOT));
        return new Expense(id, description, amount, date, category);
    }

    private void printExpenses(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        expenses.forEach(System.out::println);
    }
}
