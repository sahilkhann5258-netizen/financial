package com.example.financial.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.financial.entity.Account;
import com.example.financial.service.AccountService;

@RestController
public class AccountController {

    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create Account
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        try {
        	String username = SecurityContextHolder
        	        .getContext()
        	        .getAuthentication()
        	        .getName();
         	account.setUsername(username);
            return ResponseEntity.ok(accountService.saveAccount(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Account creation failed: " + e.getMessage());
        }
    }

    // Pagination + Filtering + Sorting + Searching
    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts(
            Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        try {
            Page<Account> accounts =
                    accountService.getAccounts(pageable, status, search);

            return ResponseEntity.ok(accounts);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error while fetching accounts: " + e.getMessage());
        }
    }
    
}