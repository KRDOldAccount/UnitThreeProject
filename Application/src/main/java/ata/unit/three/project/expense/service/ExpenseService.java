package ata.unit.three.project.expense.service;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.InvalidExpenseException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;

public class ExpenseService {

    private ExpenseServiceRepository expenseServiceRepository;
    private ExpenseItemConverter expenseItemConverter;

    @Inject
    public ExpenseService(ExpenseServiceRepository expenseServiceRepository,
                          ExpenseItemConverter expenseItemConverter) {
        this.expenseServiceRepository = expenseServiceRepository;
        this.expenseItemConverter = expenseItemConverter;
    }

    public ExpenseItem getExpenseById(String expenseId) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        return expenseServiceRepository.getExpenseById(expenseId);
    }

    public List<ExpenseItem> getExpensesByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidDataException("Email is not present");
        }
        return expenseServiceRepository.getExpensesByEmail(email);
    }

    public String createExpense(Expense expense) {
        ExpenseItem expenseItem = expenseItemConverter.convert(expense);
        expenseServiceRepository.createExpense(expenseItem);
        return expenseItem.getId();
    }

    public void updateExpense(String expenseId, Expense updateExpense) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        ExpenseItem item = expenseServiceRepository.getExpenseById(expenseId);
        if (item == null) {
            throw new ItemNotFoundException("Expense does not exist");
        }
        expenseServiceRepository.updateExpense(expenseId,
                updateExpense.getTitle(),
                updateExpense.getAmount());
    }

    public void deleteExpense(String expenseId) {
        if (StringUtils.isEmpty(expenseId) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("Expense id is not present");
        }
        expenseServiceRepository.deleteExpense(expenseId);
    }

    public String createExpenseList(String email, String title) {
//        String expenseListId = randomUUID().toString();
        String expenseListId = "c5156c9f-4306-45b6-9b39-2eeb180a702a";
        expenseServiceRepository.createExpenseList(expenseListId, email, title);
        return expenseListId;
    }

    public void addExpenseItemToList(String id, String expenseId) {
        // Your Code Here
        if(id == null) {
            throw new ItemNotFoundException("no expenseList id found");
        }
        if(expenseId == null){
            throw new ItemNotFoundException("no expense id found");
        }
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(expenseId) || isInvalidUuid(id) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("invalid inputs on either id");
        }

        ExpenseItem expenseItem = expenseServiceRepository.getExpenseById(expenseId);
        ExpenseItemList expenseItemList = expenseServiceRepository.getExpenseListById(id);

        //check if expenseItem exists
        if(expenseItem == null) {
            throw new ItemNotFoundException("no expenseList id found");
        }
        if(expenseItemList == null) {
            throw new ItemNotFoundException("No expenseItemList found given an expenseList id");
        }
        //check email match
        if(!expenseItem.getEmail().equals(expenseItemList.getEmail())) {
            throw new ItemNotFoundException("Email doesn't match");
        }
        //check if expense id in expenseIdList
        //check if expenseItemList is null
        if(expenseItemList.getExpenseItems() != null && expenseItemList.getExpenseItems().contains(expenseItem)) {
            throw new ItemNotFoundException("Already in list");
        }

        expenseServiceRepository.addExpenseItemToList(id, expenseItem);
    }

    public void removeExpenseItemToList(String id, String expenseId) {
        // Your Code Here
        if(id == null) {
            throw new ItemNotFoundException("no expenseList id found");
        }
        if(expenseId == null){
            throw new ItemNotFoundException("no expense id found");
        }
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(expenseId) || isInvalidUuid(id) || isInvalidUuid(expenseId)) {
            throw new InvalidDataException("invalid inputs on either id");
        }

        ExpenseItem expenseItem = expenseServiceRepository.getExpenseById(expenseId);
        ExpenseItemList expenseItemList = expenseServiceRepository.getExpenseListById(id);

        //check if expenseItem exists
        if(expenseItem == null) {
            throw new ItemNotFoundException("no expenseList id found");
        }
        if(expenseItemList == null) {
            throw new ItemNotFoundException("No expenseItemList found given an expenseList id");
        }
        //check email match
        if(!expenseItem.getEmail().equals(expenseItemList.getEmail())) {
            throw new ItemNotFoundException("Email doesn't match");
        }
        //check if expense id in expenseIdList
        //check if expenseItemList is null
        if(expenseItemList.getExpenseItems() != null && !expenseItemList.getExpenseItems().contains(expenseItem)) {
            throw new ItemNotFoundException("Already in list");
        }

        expenseServiceRepository.removeExpenseItemToList(id, expenseItem);
    }

    public List<ExpenseItemList> getExpenseListByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new InvalidDataException("Email is not present");
        }
        Comparator<ExpenseItem> expenseItemComparator = new ExpenseItemComparator();
        List<ExpenseItemList> sortList = expenseServiceRepository.getExpenseListsByEmail(email);
        for (ExpenseItemList sortListItems : sortList) {
            if(sortListItems.getExpenseItems() != null) {
                Collections.sort(sortListItems.getExpenseItems(), expenseItemComparator);
            }
        }

        return sortList;
    }

    private boolean isInvalidUuid(String uuid) {
        try {
            fromString(uuid);
        } catch (IllegalArgumentException exception) {
            return true;
        }
        return false;
    }

    public class ExpenseItemComparator implements Comparator<ExpenseItem> {

        @Override
        public int compare(ExpenseItem o1, ExpenseItem o2) {
            Instant expenseItemTest1 = Instant.parse(o1.getExpenseDate());
            Instant expenseItemTest2 = Instant.parse(o2.getExpenseDate());

            return expenseItemTest1.compareTo(expenseItemTest2);
        }
    }
}
