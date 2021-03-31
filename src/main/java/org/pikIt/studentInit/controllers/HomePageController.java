package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomePageController {


    @GetMapping("/")
    public String homePage() {
        return "home/homePage";
    }

    @PostMapping(("homePage"))
    public String userRoleHomePage() {
        return "redirect:/users/userPage";
    }
}
