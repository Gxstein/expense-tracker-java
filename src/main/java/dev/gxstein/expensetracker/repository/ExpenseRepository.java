package dev.gxstein.expensetracker.repository;

import dev.gxstein.expensetracker.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByOwnerIdOrderByDateDescIdDesc(Long ownerId);

    List<Expense> findAllByOwnerIdAndDateBetweenOrderByDateDescIdDesc(
            Long ownerId, LocalDate startDate, LocalDate endDate);

    Optional<Expense> findByIdAndOwnerId(Long id, Long ownerId);

    long countByOwnerIdAndDateBetween(Long ownerId, LocalDate startDate, LocalDate endDate);

    @Query("""
            select coalesce(sum(expense.amount), 0)
            from Expense expense
            where expense.owner.id = :ownerId
              and expense.date between :startDate and :endDate
            """)
    BigDecimal sumAmountByOwnerAndDateRange(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
