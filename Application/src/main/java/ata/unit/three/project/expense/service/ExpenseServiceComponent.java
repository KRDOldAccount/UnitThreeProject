package ata.unit.three.project.expense.service;

import ata.unit.three.project.App;
import dagger.Component;

@Component (modules = {App.class})
public interface ExpenseServiceComponent {
    public ExpenseService expenseService();

}
