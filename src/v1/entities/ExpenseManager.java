package v1.entities;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {

    private List<Expense> expenses = new ArrayList<>();



    public void add(Expense expense) {
        expenses.add(expense);
    }

    public void remove(Long id){
        for(Expense e : expenses){
            if (e.getId().equals(id)) {
                expenses.remove(e);
                break;
            }

        }

    }

    public List<Expense> listAll(){
        return expenses;
    }

    public List<Expense> filterByMonth(int month, int year) {
        List<Expense> expenseMonthYear = new ArrayList<>();
        for (Expense e: expenses) {
            if (e.getDate().getMonthValue() == month && e.getDate().getYear() == year) {
            expenseMonthYear.add(e);
            }
        }

        return expenseMonthYear;
    }



}
