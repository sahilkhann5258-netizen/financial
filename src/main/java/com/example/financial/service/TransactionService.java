package com.example.financial.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.financial.entity.Account;
import com.example.financial.entity.Ledger;
import com.example.financial.entity.Transaction;
import com.example.financial.exception.AccountNotFoundException;
import com.example.financial.exception.InsufficientBalanceException;
import com.example.financial.exception.InvalidAmountException;
import com.example.financial.repository.AccountRepository;
import com.example.financial.repository.LedgerRepository;
import com.example.financial.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    TransactionRepository transactionRepository;
    AccountRepository accountRepository;
    LedgerRepository ledgerRepository;
    AuditLogService auditLogService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            LedgerRepository ledgerRepository,
            AuditLogService auditLogService) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
        this.auditLogService = auditLogService;
    }

    // Get logged-in username
    private String getCurrentUsername() {

        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    public Transaction saveTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    // Get transactions
    public Page<Transaction> getTransactions(
            Pageable pageable,
            String type,
            String status,
            Long accountId,
            String search) {

        String username = getCurrentUsername();

        boolean isAdmin = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        try {

            // Admin gets all transactions
            if (isAdmin) {

                // Filter by type
                if (type != null) {

                    return transactionRepository
                            .findByTypeContainingIgnoreCase(
                                    type,
                                    pageable);
                }

                // Filter by status
                if (status != null) {

                    return transactionRepository
                            .findByStatusContainingIgnoreCase(
                                    status,
                                    pageable);
                }

                // Filter by account ID
                if (accountId != null) {

                    return transactionRepository
                            .findByAccountId(
                                    accountId,
                                    pageable);
                }

                // Search transactions
                if (search != null && !search.isBlank()) {

                    return transactionRepository
                            .globalSearch(
                                    search,
                                    pageable);
                }

                return transactionRepository.findAll(pageable);
            }

            // User gets only own transactions
            List<Account> accounts =
                    accountRepository.findByUsername(username);

            List<Long> accountIds = accounts.stream()
                    .map(Account::getId)
                    .toList();

            // Filter by type
            if (type != null) {

                return transactionRepository
                        .findByAccountIdInAndType(
                                accountIds,
                                type,
                                pageable);
            }

            // Filter by status
            if (status != null) {

                return transactionRepository
                        .findByAccountIdInAndStatus(
                                accountIds,
                                status,
                                pageable);
            }

            // Filter by account ID
            if (accountId != null) {

                if (!accountIds.contains(accountId)) {

                    throw new RuntimeException("Access denied");
                }

                return transactionRepository
                        .findByAccountId(
                                accountId,
                                pageable);
            }

            // Search transactions
            if (search != null && !search.isBlank()) {

                return transactionRepository
                        .findByAccountIdInAndTypeContainingIgnoreCase(
                                accountIds,
                                search,
                                pageable);
            }

            return transactionRepository
                    .findByAccountIdIn(
                            accountIds,
                            pageable);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error while fetching transactions: "
                            + e.getMessage());
        }
    }

    // Deposit
    @Transactional
    public Transaction deposit(Long accountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Amount must be greater than zero");
        }

        String username = getCurrentUsername();

        Account account = accountRepository
                .findByIdAndUsername(accountId, username)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found or access denied"));

        account.setBalance(
                account.getBalance().add(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccountId(accountId);
        transaction.setUsername(username);
        transaction.setType("Deposit");
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");

        // Set transaction date and time
        transaction.setTransactionDateTime(
                LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        Ledger ledger = new Ledger();

        ledger.setAccountId(accountId);
        ledger.setTransactionId(savedTransaction.getId());
        ledger.setType("CREDIT");
        ledger.setAmount(amount);

        ledgerRepository.save(ledger);

        auditLogService.log(
                username,
                "DEPOSIT",
                savedTransaction.getId()
        );

        return savedTransaction;
    }

    // Withdrawal
    @Transactional
    public Transaction withdrawal(
            Long accountId,
            BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Amount must be greater than zero");
        }

        String username = getCurrentUsername();

        Account account = accountRepository
                .findByIdAndUsername(accountId, username)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found or access denied"));

        if (account.getBalance().compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        account.setBalance(
                account.getBalance().subtract(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccountId(accountId);
        transaction.setUsername(username);
        transaction.setType("Withdrawal");
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");

        // Set transaction date and time
        transaction.setTransactionDateTime(
                LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        Ledger ledger = new Ledger();

        ledger.setAccountId(accountId);
        ledger.setTransactionId(savedTransaction.getId());
        ledger.setType("DEBIT");
        ledger.setAmount(amount);

        ledgerRepository.save(ledger);

        auditLogService.log(
                username,
                "WITHDRAWAL",
                savedTransaction.getId()
        );

        return savedTransaction;
    }

    // Transfer
    @Transactional
    public Transaction transfer(
            Long senderId,
            Long receiverId,
            BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Amount must be greater than zero");
        }

        String username = getCurrentUsername();

        // Get sender account
        Account sender = accountRepository
                .findByIdAndUsername(senderId, username)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Sender account not found or access denied"));

        // Get receiver account
        Account receiver = accountRepository
                .findById(receiverId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Receiver account not found"));

        // Calculate fee and GST
        BigDecimal fee =
                amount.multiply(new BigDecimal("0.015"));

        BigDecimal gst =
                fee.multiply(new BigDecimal("0.18"));

        BigDecimal totalDebit =
                amount.add(fee).add(gst);

        if (sender.getBalance().compareTo(totalDebit) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        sender.setBalance(
                sender.getBalance().subtract(totalDebit));

        accountRepository.save(sender);

        receiver.setBalance(
                receiver.getBalance().add(amount));

        accountRepository.save(receiver);

        Transaction transaction = new Transaction();

        transaction.setAccountId(senderId);
        transaction.setUsername(username);
        transaction.setType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");
        transaction.setReceiverId(receiverId);

        // Set transaction date and time
        transaction.setTransactionDateTime(
                LocalDateTime.now());

        // Check fraud amount
        if (amount.compareTo(
                new BigDecimal("100000")) > 0) {

            transaction.setFraudFlag(true);

        } else {

            transaction.setFraudFlag(false);
        }

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        Ledger debitLedger = new Ledger();

        debitLedger.setAccountId(senderId);
        debitLedger.setTransactionId(
                savedTransaction.getId());
        debitLedger.setType("DEBIT");
        debitLedger.setAmount(amount);

        ledgerRepository.save(debitLedger);

        Ledger creditLedger = new Ledger();

        creditLedger.setAccountId(receiverId);
        creditLedger.setTransactionId(
                savedTransaction.getId());
        creditLedger.setType("CREDIT");
        creditLedger.setAmount(amount);

        ledgerRepository.save(creditLedger);

        auditLogService.log(
                username,
                "TRANSFER",
                savedTransaction.getId()
        );

        return savedTransaction;
    }

    // Reversal
    @Transactional
    public Transaction reversal(Long transactionId) {

        String username = getCurrentUsername();

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Transaction not found"));

        // Check transaction belongs to user
        Account sender = accountRepository
                .findByIdAndUsername(
                        transaction.getAccountId(),
                        username)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Transaction does not belong to your account"));

        Account receiver = accountRepository
                .findById(transaction.getReceiverId())
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Receiver account not found"));

        sender.setBalance(
                sender.getBalance()
                        .add(transaction.getAmount()));

        receiver.setBalance(
                receiver.getBalance()
                        .subtract(transaction.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        transaction.setStatus("REVERSED");
        transaction.setType("REVERSAL");
        transaction.setUsername(username);

        // Set reversal date and time
        transaction.setTransactionDateTime(
                LocalDateTime.now());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        auditLogService.log(
                username,
                "REVERSAL",
                savedTransaction.getId()
        );

        return savedTransaction;
    }
}