package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserListController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user-list")
    public String updateUser(@ModelAttribute("user") User user) {
        return "redirect:/edit-user?username=" + user.getUsername();
    }

    @GetMapping("/user-list")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAllDesc("id"));
        return "user-list";
    }

    @GetMapping("/delete-user")
    @Transactional
    public String deleteUser(@RequestParam String username) {
        userRepository.deleteByUsername(username);
        return "redirect:/user-list";
    }

}
