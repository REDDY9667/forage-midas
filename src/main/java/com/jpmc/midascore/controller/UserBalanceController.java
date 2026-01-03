package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserBalanceController {

    private final UserService userService;

    public UserBalanceController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        return userService.getBalanceByUserId(userId);
    }
}
