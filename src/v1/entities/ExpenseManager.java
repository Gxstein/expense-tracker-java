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
}
