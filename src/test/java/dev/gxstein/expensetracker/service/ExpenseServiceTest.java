package dev.gxstein.expensetracker.service;

import dev.gxstein.expensetracker.api.dto.ExpenseRequest;
import dev.gxstein.expensetracker.domain.Category;
import dev.gxstein.expensetracker.domain.Expense;
import dev.gxstein.expensetracker.domain.User;
import dev.gxstein.expensetracker.repository.ExpenseRepository;
import dev.gxstein.expensetracker.repository.UserRepository;
import dev.gxstein.expensetracker.service.exception.InvalidExpenseFilterException;
import dev.gxstein.expensetracker.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenses;

    @Mock
    private UserRepository users;

    @InjectMocks
    private ExpenseService service;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User("Marcio", "marcio@example.com", "encoded-password");
    }

    @Test
    void createTrimsDescriptionAndAssociatesTheAuthenticatedOwner() {
        when(users.findById(42L)).thenReturn(Optional.of(owner));
        when(expenses.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ExpenseRequest request = request("  Lunch  ");

        service.create(42L, request);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenses).save(captor.capture());
        assertEquals("Lunch", captor.getValue().getDescription());
        assertEquals(owner, captor.getValue().getOwner());
    }

    @Test
    void listUsesInclusiveMonthBoundariesAndOwnerScope() {
        when(expenses.findAllByOwnerIdAndDateBetweenOrderByDateDescIdDesc(
                42L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)))
                .thenReturn(java.util.List.of());

        service.list(42L, 2, 2026);

        verify(expenses).findAllByOwnerIdAndDateBetweenOrderByDateDescIdDesc(
                42L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
    }

    @Test
    void listRejectsPartialPeriodWithoutQueryingRepository() {
        assertThrows(InvalidExpenseFilterException.class, () -> service.list(42L, 2, null));

        verifyNoInteractions(expenses);
    }

    @Test
    void findByIdUsesOwnerIdToPreventCrossAccountAccess() {
        when(expenses.findByIdAndOwnerId(7L, 42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(42L, 7L));

        verify(expenses).findByIdAndOwnerId(7L, 42L);
        verify(expenses, never()).findById(7L);
    }

    private ExpenseRequest request(String description) {
        return new ExpenseRequest(
                description,
                new BigDecimal("19.90"),
                LocalDate.of(2026, 2, 10),
                Category.FOOD);
    }
}
