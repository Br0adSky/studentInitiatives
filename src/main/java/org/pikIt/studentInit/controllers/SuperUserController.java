package org.pikIt.studentInit.controllers;

import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users/superUserPage")
@PreAuthorize("hasAuthority('SUPER_USER')")
public class SuperUserController {
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users/superUserPage";
    }

    @GetMapping("{user}")
    public String userEdit(@PathVariable User user, Model model) {
        model.addAttribute("user2", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("roles", Role.values());

        return "users/userEdit";
    }

    @PostMapping("/save")
    public String userSave(
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user) {
        Set<String> roleSet = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roleSet.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }
        userRepository.save(user);
        return "redirect:/users/superUserPage";
    }

    @PostMapping("/searchUser")
    public String searchUsers(@RequestParam String filterName,
                              @RequestParam String filterSurname,
                              Model model) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("users", userRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "Найденные пользователи");
        } else {
            model.addAttribute("message", "Пользователь не найден");
            model.addAttribute("users", userRepository.findAll());
        }
        return "users/superUserPage";
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
