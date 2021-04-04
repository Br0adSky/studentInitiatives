package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/home")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String login() {
        return "home/login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        return userService.addUserInModel(model);
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        return userService.adduser(user, bindingResult, model);
    }
}
