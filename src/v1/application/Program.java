package v1.application;

import java.util.Locale;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        Scanner sc = new Scanner(System.in);

        int select = 0;

        System.out.println("--Welcome to your Expense Tracker--");

        do {

            System.out.println("1 - Add expense");
            System.out.println("2 - Remove expense");
            System.out.println("3 - List all expenses");
            System.out.println("4 - Filter by month");
            System.out.println("5 - Show total");
            System.out.println("0 - Exit");
            System.out.print("Choose an option: ");

            select = sc.nextInt();

            switch (select){
                case 0:
                    break;
                case 1:
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
    }
}
