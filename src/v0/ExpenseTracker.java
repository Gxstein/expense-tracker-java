package v0;

import java.util.Scanner;

public class ExpenseTracker {
    public static void main(String[] args) {

        int select = 0;
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
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;

                default:
                    System.out.println("(Enter a valid number between 0 and 4)");
            }

        } while (select != 0);

        System.out.println("The program ends here - see you next time!");

        sc.close();

    }
}
