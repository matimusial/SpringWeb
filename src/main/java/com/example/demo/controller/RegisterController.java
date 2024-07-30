package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

import static com.example.demo.utils.ExceptionUtils.getRootCause;

@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (!user.getPassword().equals(user.getConPassword())) {
            model.addAttribute("conPasswordError", "Passwords do not match");
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.saveNewUser(user);
        } catch (DataIntegrityViolationException e) {
            String rootCauseMessage = getRootCause(e).getMessage();
            if (rootCauseMessage.contains("Key (username)")) {
                result.rejectValue("username", "error.user", "Username already exists.");
            } else if (rootCauseMessage.contains("Key (email)")) {
                result.rejectValue("email", "error.user", "Email already exists.");
            }
            return "register";
        }

        return "redirect:/";
    }
}
