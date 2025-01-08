package com.example.importtomo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/connect-google")
    public String connectGoogle() {
        // Logic for Google OAuth2 connection
        return "redirect:/google-auth";
    }
}

