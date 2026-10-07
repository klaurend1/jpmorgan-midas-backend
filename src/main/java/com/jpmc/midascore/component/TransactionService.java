// src/main/java/com/jpmc/midascore/component/TransactionService.java
package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {

    private final UserRepository userRepo;
    private final TransactionRepository txRepo;
    private final RestTemplate rest;
    private final String incentiveUrl;

    public TransactionService(
            UserRepository userRepo,
            TransactionRepository txRepo,
            RestTemplate rest,
            @Value("${incentive.url:http://localhost:8080/incentive}") String incentiveUrl
    ) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.rest = rest;
        this.incentiveUrl = incentiveUrl;
    }

    @Transactional
    public void process(Transaction tx) {
        var senderOpt = userRepo.findById(tx.getSenderId());
        var recipOpt  = userRepo.findById(tx.getRecipientId());
        if (senderOpt.isEmpty() || recipOpt.isEmpty()) return;

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipOpt.get();

        float amount = tx.getAmount();
        if (sender.getBalance() < amount) return; // insufficient funds

        // --- call Incentive API ---
        float incentiveAmt = 0f;
        try {
            Incentive resp = rest.postForObject(incentiveUrl, tx, Incentive.class);
            if (resp != null) incentiveAmt = Math.max(0f, resp.getAmount());
        } catch (Exception e) {
            // no incentive if API down – continue safely
            incentiveAmt = 0f;
        }

        // --- apply balances ---
        sender.setBalance(sender.getBalance() - amount);                 // only amount debited
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmt); // amount + incentive credited

        // --- persist atomically ---
        txRepo.save(new TransactionRecord(sender, recipient, amount, incentiveAmt));
        userRepo.save(sender);
        userRepo.save(recipient);
    }
}
