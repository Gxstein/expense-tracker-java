package v0;

import java.util.Locale;
import java.util.Scanner;

public class ExpenseTracker {
    public static void main(String[] args) {

        double total = 0;
        double largest_expense = 0;
        int count = 0;

        int select = 0;

        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        System.out.println("--Welcome to your Expense Tracker--");

        do {


            System.out.println("1 - Add expense");
            System.out.println("2 - Show total");
            System.out.println("3 - Show highest expense");
            System.out.println("4 - Show number of expenses");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");

            select = sc.nextInt();

            switch (select){
                case 0:
                    break;
                case 1:
                    System.out.print("Type how much you spent: ");
                    double value = sc.nextDouble();
                    if (value > 0){
                        total += value;
                        count++;
                        System.out.println("Expense added successfully!");
                    }else if(value <= 0){
                        System.out.println("Error adding expense!");
                    }
                    if (value > largest_expense){
                        largest_expense = value;
                    }
                    break;
                case 2:
                    System.out.printf("Total R$ %.2f%n", total);
                    break;
                case 3:
                    System.out.printf("Highest expense: R$ %.2f%n", largest_expense);
                    break;
                case 4:
                    System.out.printf("Number of expenses: %d%n", count);
                    break;

                default:
                    System.out.println("(Enter a valid number between 0 and 4)");
            }

        } while (select != 0);

        System.out.println("The program ends here - see you next time!");

        sc.close();

    }
}
