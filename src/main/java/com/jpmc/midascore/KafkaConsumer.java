package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @Autowired
    private TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

//    private int transactionCount = 0;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "trader-updates-group")
    public void listen(Transaction transaction) {

//        ****** TASK TWO *****

//        transactionCount++;
//
//        logger.info("========================================");
//        logger.info("Received Transaction #{}", transactionCount);
//        logger.info("Transaction Details: {}", transaction);
//        logger.info("Amount: {}", transaction.getAmount());
//        logger.info("========================================");
//
//        // Set a breakpoint HERE to inspect transactions in debugger
//        System.out.println("DEBUG: Transaction #" + transactionCount + " - Amount: " + transaction.getAmount());


//       ****** TASK THREE ******


//        logger.info("Received: {}", transaction);
//        transactionService.processTransaction(transaction);

        //       ****** TASK FOUR ******

        transactionService.processTransactionWithIncentive(transaction);
    }

//    public int getTransactionCount() {
//        return transactionCount;
//    }
}