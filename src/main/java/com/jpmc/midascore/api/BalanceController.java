package com.jpmc.midascore.api;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepo;

    public BalanceController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        float amount = userRepo.findById(userId)
                .map(UserRecord::getBalance)
                .orElse(0f);
        return new Balance(amount);  // Jackson will JSON-serialize this
    }
}
