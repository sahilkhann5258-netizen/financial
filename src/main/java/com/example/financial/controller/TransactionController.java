
package com.example.financial.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.financial.entity.Transaction;
import com.example.financial.service.TransactionService;

@RestController
public class TransactionController {

    TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {

        this.transactionService = transactionService;

    }

    @PostMapping("/deposit")
    public Object deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        try {

            return transactionService.deposit(accountId, amount);

        } catch (Exception e) {

            return "Deposit failed: " + e.getMessage();

        }

    }

    @PostMapping("/withdrawal")
    public Object withdrawal(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        try {

            return transactionService.withdrawal(accountId, amount);

        } catch (Exception e) {

            return "Withdrawal failed: " + e.getMessage();

        }

    }

    @PostMapping("/transfer")
    public Object transfer(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount) {

        try {

            return transactionService.transfer(
                    senderId,
                    receiverId,
                    amount
            );

        } catch (Exception e) {

            return "Transfer failed: " + e.getMessage();

        }

    }

    @PostMapping("/reversal")
    public Object reversal(
            @RequestParam Long transactionId) {

        try {

            return transactionService.reversal(transactionId);

        } catch (Exception e) {

            return "Reversal failed: " + e.getMessage();

        }

    }

    // USER - Own transactions
    @GetMapping("/transactions")
    public Object getTransactions(
            Pageable pageable,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String search) {

        try {

            return transactionService.getTransactions(
                    pageable,
                    type,
                    status,
                    accountId,
                    search
            );

        } catch (Exception e) {

            return "Error while fetching transactions: "
                    + e.getMessage();

        }

    }

    
    

}

