package com.fashion.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionController {
    
    //Show the subscription / pricing page
    @GetMapping("/subscribe")
    public String showSubscribepage() {
        return "subscribe";  // loads subscribe.html from templates
    }
}
