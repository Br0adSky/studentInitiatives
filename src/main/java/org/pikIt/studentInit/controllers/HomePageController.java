package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomePageController {
    private final UserService userService;

    @Autowired
    public HomePageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage() {
        userService.adminInit();
        return "home/homePage";
    }

    @PostMapping(("homePage"))
    public String userRoleHomePage() {
        return "redirect:/users/userPage";
    }
}
