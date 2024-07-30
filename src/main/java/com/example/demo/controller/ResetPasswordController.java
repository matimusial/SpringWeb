package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ResetPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public ResetPasswordController(UserRepository userRepository, EmailService emailService, UserService userService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/password/reset-password")
    public String showResetPasswordForm() {
        return "password/reset-password";
    }

    @PostMapping("/password/reset-password")
    public String handleResetPassword(@RequestParam String username, @RequestParam String email, Model model) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            model.addAttribute("error", "Unknown username");
            return "password/reset-password";
        }

        User user = userOpt.get();
        if (!user.getEmail().equals(email)) {
            model.addAttribute("error", "Invalid email address");
            return "password/reset-password";
        }

        String confirmationCode = UUID.randomUUID().toString();
        user.setConfirmationCode(confirmationCode);
        user.setConfirmationCodeExpiry(LocalDateTime.now().plusMinutes(15));
        userService.updateUser(user);
        //userService.saveConfirmationCode(user, confirmationCode);
        System.out.println("New password reset code: " + confirmationCode);

        try {
            emailService.sendConfirmationCodeEmail(user.getEmail(), confirmationCode, user.getUsername());
        } catch (MessagingException e) {
            model.addAttribute("error", "Failed to send email");
            return "password/reset-password";
        }

        return "redirect:/password/confirmation-code";
    }

    @GetMapping("/password/confirmation-code")
    public String showConfirmationCodeForm() {
        return "password/confirmation-code";
    }

    @PostMapping("/password/confirmation-code")
    public String handleConfirmationCode(@RequestParam String code, Model model) {
        Optional<User> userOpt = userRepository.findByConfirmationCode(code);
        if (!userOpt.isPresent()) {
            model.addAttribute("error", "Error with code");
            return "password/confirmation-code";
        }

        User user = userOpt.get();
        if (user.getConfirmationCodeExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Confirmation code has expired");
            return "password/confirmation-code";
        }

        return "redirect:/password/set-new-password?confirmationCode=" + user.getConfirmationCode();
    }

    @GetMapping("/password/set-new-password")
    public String showNewPasswordForm(@RequestParam String confirmationCode, Model model) {
        User user = new User();
        user.setConfirmationCode(confirmationCode);
        model.addAttribute("user", user);
        return "password/set-new-password";
    }


    @PostMapping("/password/set-new-password")
    public String handleNewPassword(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {

        if (!user.getPassword().equals(user.getConPassword())) {
            System.out.println(user.getPassword());
            System.out.println(user.getConPassword());
            model.addAttribute("error", "Passwords do not match");
            return "password/set-new-password";
        }

        if (result.hasErrors()) {
            return "password/set-new-password";
        }

        Optional<User> userOpt = userRepository.findByConfirmationCode(user.getConfirmationCode());
        if (!userOpt.isPresent()) {
            model.addAttribute("error", "Invalid confirmation code");
            return "password/set-new-password";
        }

        //userService.updatePassword(userOpt.get(), user.getPassword());
        User User = userOpt.get();
        userService.updateUser(userOpt.get());

        return "redirect:/login";
    }
}
