package com.fashion.service;

import com.fashion.model.CustomerTransaction;
import com.fashion.repository.CustomerTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerTransactionService {

    private final CustomerTransactionRepository repo;

    public CustomerTransactionService(CustomerTransactionRepository repo) {
        this.repo = repo;
    }

    // SAVE NEW TRANSACTION
    public void save(CustomerTransaction t) {
        repo.save(t);
    }

    // ALL TRANSACTIONS FOR LOGGED-IN USER
    public List<CustomerTransaction> findForUser(Long userId) {
        return repo.findByUserId(userId);
    }

    // FIND A SINGLE RECORD (FOR EDIT / VIEW / DELETE)
    public CustomerTransaction findByIdAndUser(Long id, Long userId) {
        return repo.findByIdAndUserId(id, userId);
    }

    // UPDATE EXISTING TRANSACTION
    public void updateTransaction(Long id, Long userId, CustomerTransaction updated) {
        CustomerTransaction t = repo.findByIdAndUserId(id, userId);
        if (t == null) return;  // record not found or doesn't belong to user

        t.setName(updated.getName());
        t.setPhoneNumber(updated.getPhoneNumber());
        t.setEmail(updated.getEmail());
        t.setTypeOfCloth(updated.getTypeOfCloth());
        t.setColor(updated.getColor());
        t.setMaterial(updated.getMaterial());
        t.setQuantity(updated.getQuantity());
        t.setPurchasePrice(updated.getPurchasePrice());
        t.setDepositeAmount(updated.getDepositeAmount());
        t.setRemainingAmount(updated.getRemainingAmount());
        t.setPickupDateTime(updated.getPickupDateTime());
        t.setStatus(updated.getStatus());
        t.setNote(updated.getNote());

        repo.save(t);
    }

    // DELETE TRANSACTION
    public void delete(Long id, Long userId) {
        CustomerTransaction t = repo.findByIdAndUserId(id, userId);
        if (t != null) {
            repo.delete(t);
        }
    }

    // SEARCH / FILTER (USED ON LIST PAGE)
    public List<CustomerTransaction> searchForUser(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findForUser(userId);
        }
        String pattern = "%" + keyword.toLowerCase() + "%";
        return repo.search(userId, pattern);
    }

    // CUSTOMER HISTORY (all transactions for same customer phone)
    public List<CustomerTransaction> getCustomerHistory(Long userId, Long transactionId) {

        // 1. Load the transaction you're viewing
        CustomerTransaction main = repo.findByIdAndUserId(transactionId, userId);
        if (main == null) {
            return Collections.emptyList();
        }

        // 2. Use phone number as unique customer identifier
        String phone = main.getPhoneNumber();
        if (phone == null || phone.isBlank()) {
            return Collections.singletonList(main);
        }

        // 3. Load all transactions for THIS user + THIS phone (sorted newest → oldest)
        return repo.findByUserIdAndPhoneNumberOrderByAddDateTimeDesc(userId, phone);
    }
}





