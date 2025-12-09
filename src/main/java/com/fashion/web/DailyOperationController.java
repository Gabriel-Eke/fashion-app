package com.fashion.web;

import com.fashion.model.DailyOperation;
import com.fashion.repository.DailyOperationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/daily-operations")
public class DailyOperationController {

    private final DailyOperationRepository dailyOperationRepository;

    public DailyOperationController(DailyOperationRepository dailyOperationRepository) {
        this.dailyOperationRepository = dailyOperationRepository;
    }

    // ====== LIST + DASHBOARD + FILTERS ======
    @GetMapping
    public String listDailyOperations(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model
    ) {
        Long userId = (Long) session.getAttribute("userId");
        LocalDate today = LocalDate.now();

        if (month == null) month = today.getMonthValue();
        if (year == null) year = today.getYear();

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());


        // ===== BASE QUERY (already filtered by user) =====
        List<DailyOperation> operations;

        if (date != null) {
            operations = dailyOperationRepository
                    .findByOperationDateAndUserId(date, userId);
        } else {
            operations = dailyOperationRepository
                    .findByOperationDateBetweenAndUserId(startOfMonth, endOfMonth, userId);
        }


        // ===== Additional Filters (optional) =====
        if (category != null && !category.isBlank()) {
            String catLower = category.toLowerCase();
            operations = operations.stream()
                    .filter(o -> o.getCategory() != null &&
                                 o.getCategory().toLowerCase().contains(catLower))
                    .toList();
        }

        if (transactionType != null && !transactionType.isBlank()) {
            String typeUpper = transactionType.toUpperCase();
            operations = operations.stream()
                    .filter(o -> o.getTransactionType() != null &&
                                 o.getTransactionType().equalsIgnoreCase(typeUpper))
                    .toList();
        }


        // ===== Monthly Totals (database level — secure) =====
        BigDecimal totalIncome = dailyOperationRepository
                .sumAmountByTypeAndDateBetween("INCOME", startOfMonth, endOfMonth, userId);

        BigDecimal totalExpense = dailyOperationRepository
                .sumAmountByTypeAndDateBetween("EXPENSE", startOfMonth, endOfMonth, userId);

        BigDecimal net = totalIncome.subtract(totalExpense);


        // ===== Chart: Daily Income =====
        List<Object[]> dailyIncomeRaw =
                dailyOperationRepository.findDailyTotalsByTypeBetween(
                        "INCOME", startOfMonth, endOfMonth, userId
                );

        List<String> dailyLabels = new ArrayList<>();
        List<BigDecimal> dailyIncomeTotals = new ArrayList<>();

        for (Object[] row : dailyIncomeRaw) {
            LocalDate d = (LocalDate) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            dailyLabels.add(d.toString());
            dailyIncomeTotals.add(sum);
        }


        // ===== Chart: Expense by Category =====
        List<Object[]> categoryRaw =
                dailyOperationRepository.findCategoryTotalsByTypeBetween(
                        "EXPENSE", startOfMonth, endOfMonth, userId
                );

        List<String> categoryLabels = new ArrayList<>();
        List<BigDecimal> categoryTotals = new ArrayList<>();

        for (Object[] row : categoryRaw) {
            String cat = (String) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            if (cat == null || cat.isBlank()) cat = "Uncategorized";
            categoryLabels.add(cat);
            categoryTotals.add(sum);
        }


        // ===== Model =====
        model.addAttribute("operations", operations);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedType", transactionType);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);

        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("net", net);
        model.addAttribute("totalCount", operations.size());

        model.addAttribute("dailyLabels", dailyLabels);
        model.addAttribute("dailyIncomeTotals", dailyIncomeTotals);
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryTotals", categoryTotals);

        model.addAttribute("years", List.of(year - 1, year, year + 1));
        model.addAttribute("months", List.of(1,2,3,4,5,6,7,8,9,10,11,12));

        return "daily-operations";
    }


    // ====== SAVE NEW OPERATION ======
    @PostMapping
    public String saveDailyOperation(
            @ModelAttribute DailyOperation dailyOperation,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        dailyOperation.setUserId(userId);

        if (dailyOperation.getOperationDate() == null) {
            dailyOperation.setOperationDate(LocalDate.now());
        }

        if (dailyOperation.getTransactionType() != null) {
            dailyOperation.setTransactionType(
                    dailyOperation.getTransactionType().toUpperCase()
            );
        }

        dailyOperationRepository.save(dailyOperation);
        return "redirect:/daily-operations";
    }


    // ====== LOAD EDIT PAGE ======
    @GetMapping("/edit/{id}")
    public String editOperation(@PathVariable Long id, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        DailyOperation op = dailyOperationRepository.findById(id)
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation ID or access denied"));

        model.addAttribute("operation", op);
        return "daily-operation-edit";
    }


    // ====== UPDATE OPERATION ======
    @PostMapping("/update")
    public String updateOperation(@ModelAttribute DailyOperation updatedOp, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        DailyOperation existing = dailyOperationRepository.findById(updatedOp.getId())
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation ID or permission denied"));

        existing.setOperationDate(updatedOp.getOperationDate());
        existing.setTransactionType(updatedOp.getTransactionType().toUpperCase());
        existing.setAmount(updatedOp.getAmount());
        existing.setCategory(updatedOp.getCategory());
        existing.setCustomerName(updatedOp.getCustomerName());
        existing.setDescription(updatedOp.getDescription());

        dailyOperationRepository.save(existing);

        return "redirect:/daily-operations";
    }
}



