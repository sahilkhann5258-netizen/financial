package com.example.financial.service;

import java.math.BigDecimal;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.financial.entity.Account;
import com.example.financial.repository.AccountRepository;

@Service
public class InterestService {

    private AccountRepository accountRepository;

    public InterestService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void calculateMonthlyInterest() {

        for (Account account : accountRepository.findAll()) {

            BigDecimal monthlyRate = new BigDecimal("0.04")
                    .divide(new BigDecimal("12"));

            BigDecimal interest = account.getBalance()
                    .multiply(monthlyRate);

            account.setBalance(
                    account.getBalance().add(interest)
            );

            accountRepository.save(account);
        }
    }
}