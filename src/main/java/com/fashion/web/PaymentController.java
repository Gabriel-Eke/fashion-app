package com.fashion.web;

import com.fashion.model.User;
import com.fashion.service.UserService;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${stripe.secret-key}")
    private String stripeSecret;

    private final UserService userService;

    public PaymentController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, String> createCheckout(@RequestParam String plan, HttpSession session) throws Exception {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Map.of("error", "Not logged in");
        }

        // Set Stripe secret key
        Stripe.apiKey = stripeSecret;

        // Determine plan price ID
        String priceId = switch (plan) {
            case "basic" -> "price_basic123";   // TODO: Replace with real Stripe price
            case "pro" -> "price_pro123";       // TODO: Replace with real Stripe price
            default -> throw new IllegalArgumentException("Invalid plan selected");
        };

        // Stripe 24.x uses SessionCreateParams
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl("https://your_domain.com/payment/success")  // Add your domain url here
                        .setCancelUrl("https://your_domain.com/payment/cancel")     // Add your domain url here
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(priceId)
                                        .setQuantity(1L)
                                        .build()
                        )
                        .build();

        // Create checkout session
        Session checkoutSession = Session.create(params);

        return Map.of("url", checkoutSession.getUrl());
    }

    @GetMapping("/success")
    public String sucessPage() {
        return "payment-success";
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "payment-cancel";
    }
}


