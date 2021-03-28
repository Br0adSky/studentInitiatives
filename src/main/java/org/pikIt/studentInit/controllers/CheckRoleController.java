package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckRoleController {
    @GetMapping("/")
    public String homePage() {
        return "home/homePage";
    }

    @PostMapping(("homePage"))
    public String userRole(@AuthenticationPrincipal User user) {
        if (user.getRoles().contains(Role.SUPER_USER))
            return "redirect:/users/";
        else if (user.getRoles().contains(Role.MODERATOR))
            return "redirect:/bids";
        else if (user.getRoles().contains(Role.EXPERT))
            return "redirect:/users/userPage";
        else
            return "redirect:/users/userPage";
    }
}
