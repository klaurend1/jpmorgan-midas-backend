package com.jpmc.midascore.kafka;

import com.jpmc.midascore.component.TransactionService;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // topic comes from application.yml -> general.kafka-topic
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void onMessage(Transaction tx) {
        // set a breakpoint here if you want to watch each commit
        transactionService.process(tx);
    }
}
