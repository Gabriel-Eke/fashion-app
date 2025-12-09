package com.fashion.web;

import com.fashion.model.Inventory;
import com.fashion.repository.InventoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final InventoryRepository inventoryRepository;

    public HomeController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        // Get logged-in user (if you use login system)
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            // Get user inventory items that are low stock (<5)
            List<Inventory> lowStockItems = inventoryRepository.findByUserId(userId)
                    .stream()
                    .filter(Inventory::isLowStock)
                    .toList();

            // Send list to the frontend
            model.addAttribute("lowStock", lowStockItems);
        }

        return "home";  // loads home.html
    }
}

