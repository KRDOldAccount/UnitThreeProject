package ata.unit.three.project.expense.service;

import ata.unit.three.project.expense.dynamodb.ExpenseItem;
import ata.unit.three.project.expense.dynamodb.ExpenseItemList;
import ata.unit.three.project.expense.dynamodb.ExpenseServiceRepository;
import ata.unit.three.project.expense.lambda.models.Expense;
import ata.unit.three.project.expense.service.exceptions.InvalidDataException;
import ata.unit.three.project.expense.service.exceptions.ItemNotFoundException;
import ata.unit.three.project.expense.service.model.ExpenseItemConverter;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    private final MockNeat mockNeat = MockNeat.threadLocal();

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseById
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expense_by_id() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        expenseItem.setId(id);
        expenseItem.setEmail(mockNeat.emails().val());
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        //WHEN
        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);

        //THEN
        ExpenseItem returnedExpenseItem = expenseService.getExpenseById(id);
        Assertions.assertEquals(returnedExpenseItem.getId(), expenseItem.getId());
        Assertions.assertEquals(returnedExpenseItem.getEmail(), expenseItem.getEmail());
        Assertions.assertEquals(returnedExpenseItem.getTitle(), expenseItem.getTitle());
        Assertions.assertEquals(returnedExpenseItem.getExpenseDate(), expenseItem.getExpenseDate());
    }

    @Test
    void create_expense() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);


        Expense expense = new Expense(email, testTitle, amount);

        //WHEN
        when(expenseItemConverter.convert(expense)).thenReturn(expenseItem);

        //THEN
        String returnedExpenseItem = expenseService.createExpense(expense);
        assertEquals(id, returnedExpenseItem, "These should have matched");
    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.getExpensesByEmail
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expenses_by_email() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        List<ExpenseItem> expenseItemList = Collections.singletonList(expenseItem);

        //WHEN
        when(expenseServiceRepository.getExpensesByEmail(email)).thenReturn(expenseItemList);

        //THEN
        List<ExpenseItem> returnedExpenseList = expenseService.getExpensesByEmail(email);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getId(), id);
        assertEquals(returnedExpenseList.get(0).getEmail(), email);
    }

    @Test
    void get_expenses_by_email_bad_input() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail("");
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        assertThrows(InvalidDataException.class, () -> expenseService.getExpensesByEmail(""), "Expected to throw" +
                "invalid data exception - no exception thrown");

    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.updateExpense
     *  ------------------------------------------------------------------------ **/

    @Test
    void update_expense_happy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);


        Expense expense = new Expense(email, testTitle, amount);

        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);

        expenseService.updateExpense(id, expense);

        verify(expenseServiceRepository).updateExpense(id, testTitle, amount);

    }

    @Test
    void update_expense_unhappy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = "bob";
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);

        Expense expense = new Expense(email, testTitle, amount);

        assertThrows(InvalidDataException.class, () -> expenseService.updateExpense(id, expense), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);
//
//        expenseService.updateExpense(id, expense);
//
//        verify(expenseServiceRepository).updateExpense(id, testTitle, amount);

    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.deleteExpense
     *  ------------------------------------------------------------------------ **/

    @Test
    void delete_expense_happy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);

//        Expense expense = new Expense(email, testTitle, amount);

        expenseService.deleteExpense(id);

        verify(expenseServiceRepository).deleteExpense(id);

    }

    @Test
    void delete_expense_unhappy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = "bob";
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);

//        Expense expense = new Expense(email, testTitle, amount);

        assertThrows(InvalidDataException.class, () -> expenseService.deleteExpense(id), "Expected to throw" +
                "invalid data exception - no exception thrown");

    }

    @Test
    void get_expenses_by_id_unhappy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = "bob";
        expenseItem.setId(id);
        expenseItem.setEmail(mockNeat.emails().val());
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

//        expenseService.getExpenseById(id);

        assertThrows(InvalidDataException.class, () -> expenseService.getExpenseById(id), "Expected to throw" +
                "invalid data exception - no exception thrown");
    }

    @Test
    void get_expenses_by_id_unhappy_case2() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = null;
        expenseItem.setId(id);
        expenseItem.setEmail(mockNeat.emails().val());
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

//        expenseService.getExpenseById(id);

        assertThrows(InvalidDataException.class, () -> expenseService.getExpenseById(id), "Expected to throw" +
                "invalid data exception - no exception thrown");
    }

    @Test
    void delete_expense_unhappy_case2() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = null;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);

//        Expense expense = new Expense(email, testTitle, amount);

        assertThrows(InvalidDataException.class, () -> expenseService.deleteExpense(id), "Expected to throw" +
                "invalid data exception - no exception thrown");

    }

    @Test
    void update_expense_unhappy_case3() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = null;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);

        Expense expense = new Expense(email, testTitle, amount);

        assertThrows(InvalidDataException.class, () -> expenseService.updateExpense(id, expense), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);
//
//        expenseService.updateExpense(id, expense);
//
//        verify(expenseServiceRepository).updateExpense(id, testTitle, amount);

    }

    @Test
    void update_expense_unhappy_case4() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String testTitle = mockNeat.strings().val();
        expenseItem.setTitle(testTitle);
        Double amount = new Double(0.0);
        expenseItem = null;


        Expense expense = new Expense(email, testTitle, amount);

        when(expenseServiceRepository.getExpenseById(id)).thenReturn(expenseItem);

//        expenseService.updateExpense(id, expense);

        assertThrows(ItemNotFoundException.class, () -> expenseService.updateExpense(id, expense), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).updateExpense(id, testTitle, amount);

    }


    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.addExpenseItemToList
     *  ------------------------------------------------------------------------ **/
    @Test
    void add_item_to_list_happy() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
        expenseService.addExpenseItemToList(id, expenseId);

        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_one() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = null;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_two() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = null;

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_three() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = null;
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
//        expenseItem.setId(id);
//        expenseItem.setEmail(email);
//        expenseItem.setExpenseDate(Instant.now().toString());
//        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_four() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = null;
        String expenseListId = mockNeat.strings().val();
//        expenseItemList.setEmail(email);
//        expenseItemList.setTitle(mockNeat.strings().val());
//        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_five() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        String diffEmail = "bob";
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(diffEmail);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_six() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
//        ExpenseItem diffExpenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void add_item_to_list_unhappy_seven() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = "bob";
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.addExpenseItemToList(id, expenseId);

        assertThrows(InvalidDataException.class, () -> expenseService.addExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).addExpenseItemToList(id, expenseItem);

    }

    // Write additional tests here

    /** ------------------------------------------------------------------------
     *  expenseService.removeExpenseItemFromList
     *  ------------------------------------------------------------------------ **/
    @Test
    void remove_item_from_list_happy() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
        expenseService.removeExpenseItemToList(id, expenseId);

        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    // Write additional tests here
    @Test
    void remove_item_from_list_unhappy_one() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = null;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_two() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = null;

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_three() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = null;
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
//        expenseItem.setId(id);
//        expenseItem.setEmail(email);
//        expenseItem.setExpenseDate(Instant.now().toString());
//        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_four() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = null;
        String expenseListId = mockNeat.strings().val();
//        expenseItemList.setEmail(email);
//        expenseItemList.setTitle(mockNeat.strings().val());
//        expenseItemList.setId(expenseListId);
//        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
//        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_five() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
        String diffEmail = "bob";
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(diffEmail);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_six() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        ExpenseItem diffExpenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();;
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(diffExpenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(ItemNotFoundException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    @Test
    void remove_item_from_list_unhappy_seven() {
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = "bob";
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());
        String expenseId = UUID.randomUUID().toString();

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseById(expenseId)).thenReturn(expenseItem);
        when(expenseServiceRepository.getExpenseListById(id)).thenReturn(expenseItemList);

        //THEN
//        expenseService.removeExpenseItemToList(id, expenseId);

        assertThrows(InvalidDataException.class, () -> expenseService.removeExpenseItemToList(id, expenseId), "Expected to throw" +
                "invalid data exception - no exception thrown");

//        verify(expenseServiceRepository).removeExpenseItemToList(id, expenseItem);

    }

    /** ------------------------------------------------------------------------
     *  expenseService.getExpenseListByEmail
     *  ------------------------------------------------------------------------ **/

    @Test
    void get_expense_list_by_email() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        when(expenseServiceRepository.getExpenseListsByEmail(anyString())).thenReturn(list);

        //THEN
        List<ExpenseItemList> returnedExpenseList = expenseService.getExpenseListByEmail(email);
        assertEquals(returnedExpenseList.size(), 1);
        assertEquals(returnedExpenseList.get(0).getId(), expenseListId);
        assertEquals(returnedExpenseList.get(0).getEmail(), email);
    }

    // Write additional tests here
    @Test
    void get_expense_list_by_email_unhappy_case() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = "";
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        expenseItem.setTitle(mockNeat.strings().val());

        //WHEN
        ExpenseItemList expenseItemList = new ExpenseItemList();
        String expenseListId = mockNeat.strings().val();
        expenseItemList.setEmail(email);
        expenseItemList.setTitle(mockNeat.strings().val());
        expenseItemList.setId(expenseListId);
        expenseItemList.setExpenseItems(Collections.singletonList(expenseItem));
        List<ExpenseItemList> list = Collections.singletonList(expenseItemList);

        assertThrows(InvalidDataException.class, () -> expenseService.getExpenseListByEmail(email), "Expected to throw" +
                "invalid data exception - no exception thrown");
    }

    @Test
    void create_expense_list() {
        //GIVEN
        ExpenseServiceRepository expenseServiceRepository = mock(ExpenseServiceRepository.class);
        ExpenseItemConverter expenseItemConverter = mock(ExpenseItemConverter.class);
        ExpenseService expenseService = new ExpenseService(expenseServiceRepository, expenseItemConverter);

        ExpenseItem expenseItem = new ExpenseItem();
        String id = UUID.randomUUID().toString();
        String email = mockNeat.emails().val();
        expenseItem.setId(id);
        expenseItem.setEmail(email);
        expenseItem.setExpenseDate(Instant.now().toString());
        String title = mockNeat.strings().val();
        expenseItem.setTitle(title);
        Double amount = new Double(0.0);
        String expenseListId = "c5156c9f-4306-45b6-9b39-2eeb180a702a";


        Expense expense = new Expense(email, title, amount);

        //WHEN
//        when(expenseServiceRepository.createExpenseList()).thenReturn(expenseListId, email, testTitle);

        expenseService.createExpenseList(email, title);

        //THEN
        verify(expenseServiceRepository).createExpenseList(expenseListId, email, title);
    }

}