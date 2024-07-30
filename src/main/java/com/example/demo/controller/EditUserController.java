package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.demo.utils.ExceptionUtils.getRootCause;

@Controller
public class EditUserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public EditUserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/edit-user")
    public String showEditUserForm(Model model, @RequestParam String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user Username:" + username));
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/edit-user")
    public String editUser(@Valid @ModelAttribute("user") User user, @RequestParam String username, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "edit-user";
        }
        try {
            user.setUsername(username);
            userService.updateUser(user);
        } catch (DataIntegrityViolationException e) {
            String rootCauseMessage = getRootCause(e).getMessage();
            if (rootCauseMessage.contains("Key (username)")) {
                result.rejectValue("username", "error.user", "Username already exists.");
            } else if (rootCauseMessage.contains("Key (email)")) {
                result.rejectValue("email", "error.user", "Email already exists.");
            }
            model.addAttribute("user", user);
            return "edit-user";
        }
        return "redirect:/user-list";
    }

}
