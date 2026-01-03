package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.IncentiveService;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IncentiveService incentiveService;

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction from '{}' to '{}' amount: {}",
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());

        // Find sender - adjust method name based on your UserRepository
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        if (senderOpt.isEmpty()) {
            logger.warn("Invalid sender: {}", transaction.getSenderId());
            return false;
        }

        // Find recipient - adjust method name based on your UserRepository
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());
        if (recipientOpt.isEmpty()) {
            logger.warn("Invalid recipient: {}", transaction.getRecipientId());
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        float amount = transaction.getAmount();

        // Check sufficient balance
        if (sender.getBalance() < amount) {
            logger.warn("Insufficient balance. Sender {} has {}, needs {}",
                    sender.getName(), sender.getBalance(), amount);
            return false;
        }

        // Process transaction
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

//        // Save transaction record
//        TransactionRecord record = new TransactionRecord(
//                sender, recipient, amount, 0.0
//        );
//        transactionRepository.save(record);

        logger.info("✅ Transaction successful. Sender: {}, Recipient: {}",
                sender.getBalance(), recipient.getBalance());

        return true;
    }

    public void printUserBalance(String name) {
        userRepository.findByName(name).ifPresent(user ->
                logger.info("========== USER: {} | BALANCE: {} ==========", name, user.getBalance())
        );
    }

    @Transactional
    public boolean processTransactionWithIncentive(Transaction transaction){
        // Find sender - adjust method name based on your UserRepository
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        if (senderOpt.isEmpty()) {
            logger.warn("Invalid sender: {}", transaction.getSenderId());
            return false;
        }

        // Find recipient - adjust method name based on your UserRepository
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());
        if (recipientOpt.isEmpty()) {
            logger.warn("Invalid recipient: {}", transaction.getRecipientId());
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // INVALID TRANSACTION → EXIT
        if (sender == null || recipient == null) return false;
        if (sender.getBalance() < transaction.getAmount()) return false;

        // CALL INCENTIVE API ONLY AFTER VALIDATION
        float incentive = incentiveService.fetchIncentive(transaction);

        // UPDATE BALANCES
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(
                recipient.getBalance() + transaction.getAmount() + incentive
        );

        // SAVE RECORD
        TransactionRecord record =
                new TransactionRecord(sender, recipient, transaction.getAmount(), incentive);

        transactionRepository.save(record);

        userRepository.save(sender);
        userRepository.save(recipient);
        return true;
    }
}