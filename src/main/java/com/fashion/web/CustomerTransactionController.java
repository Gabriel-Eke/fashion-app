package com.fashion.web;

import com.fashion.model.CustomerTransaction;
import com.fashion.service.CustomerTransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/customer-transactions")
public class CustomerTransactionController {

    private final CustomerTransactionService service;

    public CustomerTransactionController(CustomerTransactionService service) {
        this.service = service;
    }

    // ============================================================
    // SHOW ALL TRANSACTIONS + SEARCH FEATURE
    // ============================================================
    @GetMapping
    public String page(@RequestParam(value = "search", required = false) String search,
                       Model model,
                       HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("transactions", service.searchForUser(userId, search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("transactions", service.findForUser(userId));
        }

        return "customer-transactions";
    }

    // ============================================================
    // ADD NEW TRANSACTION
    // ============================================================
    @PostMapping
    public String save(@ModelAttribute CustomerTransaction t,
                       @RequestParam String pickupDateTime,
                       HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        t.setUserId(userId);
        t.setAddDateTime(LocalDateTime.now());
        t.setPickupDateTime(LocalDateTime.parse(pickupDateTime));

        service.save(t);

        return "redirect:/customer-transactions";
    }

    // ============================================================
    // SHOW EDIT PAGE
    // ============================================================
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id,
                           Model model,
                           HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        CustomerTransaction t = service.findByIdAndUser(id, userId);
        if (t == null) return "redirect:/customer-transactions";

        String pickupValue = "";
        if (t.getPickupDateTime() != null) {
            String raw = t.getPickupDateTime().toString();
            pickupValue = raw.length() >= 16 ? raw.substring(0, 16) : raw;
        }

        model.addAttribute("transaction", t);
        model.addAttribute("pickupDateTimeValue", pickupValue);

        return "edit-transaction";
    }

    // ============================================================
    // SAVE EDITED TRANSACTION
    // ============================================================
    @PostMapping("/edit/{id}")
    public String saveEdit(@PathVariable Long id,
                           @ModelAttribute CustomerTransaction updated,
                           @RequestParam String pickupDateTime,
                           HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        updated.setPickupDateTime(LocalDateTime.parse(pickupDateTime));
        service.updateTransaction(id, userId, updated);

        return "redirect:/customer-transactions";
    }

    // ============================================================
    // DELETE TRANSACTION
    // ============================================================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        service.delete(id, userId);

        return "redirect:/customer-transactions";
    }

    // ============================================================
    // VIEW CUSTOMER INFO — FULL HISTORY + TOTALS
    // ============================================================
    @GetMapping("/customer/{id}")
    public String viewCustomer(@PathVariable Long id,
                               Model model,
                               HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // 1️⃣ Load the transaction clicked
        CustomerTransaction customer = service.findByIdAndUser(id, userId);
        if (customer == null) return "redirect:/customer-transactions";


        // Totals should reflect a single transaction
        int totalOrders = 1;
        double totalAmount = customer.getPurchasePrice();
        double totalDeposited = customer.getDepositeAmount();
        double totalRemaining = customer.getRemainingAmount();

        // 4️⃣ Pass everything to the page
        model.addAttribute("customer", customer);
        model.addAttribute("history", java.util.List.of(customer));
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("totalDeposited", totalDeposited);
        model.addAttribute("totalRemaining", totalRemaining);

        return "customer-info";
    }
}





