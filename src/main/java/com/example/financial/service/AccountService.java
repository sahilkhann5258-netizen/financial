
package com.example.financial.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.financial.entity.Account;
import com.example.financial.repository.AccountRepository;

import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class AccountService {

    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {

        this.accountRepository = accountRepository;

    }

    // Create Account
    public Account saveAccount(Account account) {

        try {

            return accountRepository.save(account);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Account creation failed: " + e.getMessage());

        }

    }

    // Get all accounts - ADMIN
    public List<Account> getAllAccounts() {

        try {

            return accountRepository.findAll();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error while fetching all accounts: " + e.getMessage());

        }

    }
 // Pagination + Filtering + Sorting + Searching + Exception Handling
 public Page<Account> getAccounts(
         Pageable pageable,
         String status,
         String search) {

     String username = SecurityContextHolder
             .getContext()
             .getAuthentication()
             .getName();

     boolean isAdmin = SecurityContextHolder
             .getContext()
             .getAuthentication()
             .getAuthorities()
             .stream()
             .anyMatch(authority ->
                     authority.getAuthority().equals("ROLE_ADMIN"));

     try {

         // ADMIN → all accounts
         if (isAdmin) {

             if (status != null && search != null) {
                 return accountRepository
                         .findByStatusAndHolderNameContainingIgnoreCase(
                                 status,
                                 search,
                                 pageable);
             }

             if (status != null) {
                 return accountRepository
                         .findByStatus(
                                 status,
                                 pageable);
             }

             if (search != null) {
                 return accountRepository
                         .findByHolderNameContainingIgnoreCase(
                                 search,
                                 pageable);
             }

             return accountRepository.findAll(pageable);
         }

         // USER → only own accounts
         if (status != null && search != null) {

             return accountRepository
                     .findByUsernameAndStatusAndHolderNameContainingIgnoreCase(
                             username,
                             status,
                             search,
                             pageable);
         }

         if (status != null) {

             return accountRepository
                     .findByUsernameAndStatus(
                             username,
                             status,
                             pageable);
         }

         if (search != null) {

             return accountRepository
                     .findByUsernameAndHolderNameContainingIgnoreCase(
                             username,
                             search,
                             pageable);
         }

         return accountRepository
                 .findByUsername(username, pageable);

     } catch (Exception e) {

         throw new RuntimeException(
                 "Error while fetching accounts: " + e.getMessage());
     }
 }
}
