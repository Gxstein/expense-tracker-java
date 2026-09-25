package v1.application;

import v1.entities.Expense;
import v1.entities.ExpenseManager;
import v1.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        Scanner sc = new Scanner(System.in);

        ExpenseManager expm = new ExpenseManager();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int select = 0;

        Long id = 1L;

        System.out.println("--Welcome to your Expense Tracker--");

        do {
            System.out.println();
            System.out.println("1 - Add expense");
            System.out.println("2 - Remove expense");
            System.out.println("3 - List all expenses");
            System.out.println("4 - Filter by month");
            System.out.println("5 - Show total");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");

            select = sc.nextInt();

            switch (select) {
                case 0:
                    break;
                case 1:
                    System.out.print("Description: ");
                    sc.nextLine();
                    String description = sc.nextLine();
                    System.out.print("Amount: ");
                    BigDecimal amount = sc.nextBigDecimal();
                    System.out.print("Date (dd/MM/yyyy): ");
                    LocalDate date = LocalDate.parse(sc.next(), fmt);
                    System.out.print("Category (FOOD, TRANSPORT, HOUSING, LEISURE, HEALTH, OTHER): ");
                    Category category = Category.valueOf(sc.next());

                    Expense expense = new Expense(id, description, amount, date, category);
                    expm.add(expense);

                    System.out.printf("Expense added! (id %d)%n", id);
                    id++;
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    System.out.println("(Enter a valid number between 0 and 5)");
            }

        } while (select != 0);

        sc.close();
    }
}