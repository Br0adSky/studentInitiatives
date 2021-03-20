package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainPage {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping
    public String main(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "main";
    }

    @PostMapping
    public String addUser(@RequestParam String name, @RequestParam String email,
                          Model model) {
        User user = new User(name, email);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "main";
    }
}
