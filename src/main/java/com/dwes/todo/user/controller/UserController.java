package com.dwes.todo.user.controller;

import com.dwes.todo.user.dto.CreatedUserRequest;
import com.dwes.todo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.dwes.todo.user.model.User;

@Controller
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @GetMapping("/auth/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new CreatedUserRequest());
        return "register";
    }

    @PostMapping("/auth/register/submit")
    public String processRegistrationForm(
            @ModelAttribute("user") CreatedUserRequest request,
            BindingResult bindingResult) {

        if(!request.isPasswordMatching()){
            bindingResult.rejectValue("verifyPassword", "error.user", "Passwords do not match");
        }

        if(!request.isPasswordStrong()) {
            bindingResult.rejectValue("password", "error.user", "Passwords must be at least 8 characters and contain uppercase, lowercase and number");
        }

        if (userService.existsByUsername(request.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())){
            bindingResult.rejectValue("email", "error.user", "Email already exists");
        }

        if (bindingResult.hasErrors())
            return "register";

        User saved = userService.registerUser(request);

        return "redirect:/login";
    }
}
