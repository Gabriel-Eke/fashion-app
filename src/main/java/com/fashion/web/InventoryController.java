package com.fashion.web;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fashion.model.Inventory;
import com.fashion.repository.InventoryRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
    
    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) {
        this.repo = repo;
    }

    // ========= LIST + DASHBOARD ==============
    @GetMapping
    public String listInventory(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            // optional: redirect to login if not logged in 
            return "redirect:/login";
        }

        List<Inventory> items = repo.findByUserId(userId);

        model.addAttribute("items", items);
        model.addAttribute("newItem", new Inventory());

        // ------- Dashboard stats -----
        int totalItems = items.size();
        int totalQuantity = items.stream().mapToInt(Inventory::getQuantity).sum();

        BigDecimal totalValue = items.stream().map(i -> {
            if (i.getUnitPrice() == null) return BigDecimal.ZERO;
            return i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStockCount = items.stream().filter(Inventory::isLowStock).count();
        long outOfStockCount = items.stream().filter(Inventory::isOutOfStock).count();

        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("outOfStockCount", outOfStockCount);

        return "inventory";
    }

    // ================= ADD NEW STOCK ==================
    @PostMapping("/add")
    public String addItem(@ModelAttribute("newItem") Inventory item, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        item.setUserId(userId);
        if (item.getQuantity() < 0) item.setQuantity(0);
        repo.save(item);

        return "redirect:/inventory";
    }

    // ================ SELL MULTIPLE QTY ===============
    @PostMapping("/sell/{id}")
    public String sellItem(@PathVariable Long id, @RequestParam("qty") int qty) {
        Inventory item = repo.findById(id).orElse(null);

        if (item != null && qty > 0 && item.getQuantity() >= qty) {
            item.setQuantity(item.getQuantity() - qty);
            repo.save(item);
        }

        return "redirect:/inventory";
    }

    // ================ DELETE ITEM ==================
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/inventory";
    }

    // ================ BARCODE SCAN SEARCH ==============
    @GetMapping("/scan")
    public String scanBarcode(@RequestParam("code") String code, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Inventory> userItems = repo.findByUserId(userId);

        Inventory found = userItems.stream()
                .filter(i -> i.getBarcode() != null && i.getBarcode().equals(code))
                .findFirst()
                .orElse(null);

        model.addAttribute("result", found);
        model.addAttribute("barcode", code);

        return "inventory-scan";
    }
}
