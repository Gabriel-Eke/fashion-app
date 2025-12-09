package com.fashion.repository;

import com.fashion.model.DailyOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DailyOperationRepository extends JpaRepository<DailyOperation, Long> {

    // ====== Get all operations for a single user on a specific date ======
    List<DailyOperation> findByOperationDateAndUserId(LocalDate date, Long userId);

    // ====== Get all operations for a user within a date range ======
    List<DailyOperation> findByOperationDateBetweenAndUserId(LocalDate start, LocalDate end, Long userId);

    // ====== Optional category filter (not required but leaving for compatibility) ======
    List<DailyOperation> findByCategoryIgnoreCaseContaining(String category);


    // ====== Monthly totals (ONLY this user) ======
    @Query("""
        SELECT COALESCE(SUM(o.amount),0)
        FROM DailyOperation o
        WHERE o.transactionType = :type
          AND o.operationDate BETWEEN :start AND :end
          AND o.userId = :userId
    """)
    BigDecimal sumAmountByTypeAndDateBetween(@Param("type") String type,
                                             @Param("start") LocalDate start,
                                             @Param("end") LocalDate end,
                                             @Param("userId") Long userId);


    // ====== Daily totals for line chart ======
    @Query("""
        SELECT o.operationDate, SUM(o.amount)
        FROM DailyOperation o
        WHERE o.transactionType = :type
          AND o.operationDate BETWEEN :start AND :end
          AND o.userId = :userId
        GROUP BY o.operationDate
        ORDER BY o.operationDate
    """)
    List<Object[]> findDailyTotalsByTypeBetween(@Param("type") String type,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end,
                                                @Param("userId") Long userId);


    // ====== Category totals (pie chart) ======
    @Query("""
        SELECT o.category, SUM(o.amount)
        FROM DailyOperation o
        WHERE o.transactionType = :type
          AND o.operationDate BETWEEN :start AND :end
          AND o.userId = :userId
        GROUP BY o.category
        ORDER BY SUM(o.amount) DESC
    """)
    List<Object[]> findCategoryTotalsByTypeBetween(@Param("type") String type,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end,
                                                   @Param("userId") Long userId);
}


