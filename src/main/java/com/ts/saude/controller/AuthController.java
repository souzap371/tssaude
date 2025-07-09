package com.ts.saude.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login"; // template login.html
    }

    @GetMapping("/")
    public String home() {
        return "index"; // página inicial após login
    }
}
