package com.fashion.repository;

import com.fashion.model.CustomerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerTransactionRepository extends JpaRepository<CustomerTransaction, Long> {

    // All transactions for a user
    List<CustomerTransaction> findByUserId(Long userId);

    // Single record for edit / delete (must belong to that user)
    CustomerTransaction findByIdAndUserId(Long id, Long userId);

    // All transactions for this user AND this phone number (customer history)
    // ⭐ Ordered newest first
    List<CustomerTransaction> findByUserIdAndPhoneNumberOrderByAddDateTimeDesc(
            Long userId,
            String phoneNumber
    );

    // Search by name, cloth type, or color
    @Query("""
           SELECT t
           FROM CustomerTransaction t
           WHERE t.userId = :userId
           AND (
                LOWER(t.name)        LIKE LOWER(:keyword)
             OR LOWER(t.typeOfCloth) LIKE LOWER(:keyword)
             OR LOWER(t.color)       LIKE LOWER(:keyword)
           )
           """)
    List<CustomerTransaction> search(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
}




