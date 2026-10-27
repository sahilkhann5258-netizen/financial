package com.example.financial.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.example.financial.entity.Account;

import jakarta.persistence.LockModeType;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdAndUsername(Long id, String username);

    Page<Account> findByUsernameAndStatus(
            String username, String status, Pageable pageable);

    Page<Account> findByUsernameAndHolderNameContainingIgnoreCase(
            String username, String holderName, Pageable pageable);

    Page<Account> findByUsernameAndStatusAndHolderNameContainingIgnoreCase(
            String username, String status, String holderName, Pageable pageable);

    Page<Account> findByUsername(String username, Pageable pageable);

    // Filtering + Pagination
    Page<Account> findByStatus(String status, Pageable pageable);

    // Searching + Pagination
    Page<Account> findByHolderNameContainingIgnoreCase(
            String holderName, Pageable pageable);

    // Filtering + Searching + Pagination
    Page<Account> findByStatusAndHolderNameContainingIgnoreCase(
            String status, String holderName, Pageable pageable);
}

/*
 * ================= ACCOUNT API TESTING =================
 *
 * 1. Create Account
 * POST http://localhost:8080/createAccount
 * Body:
 * {
 *   "accountNumber": "ACC200",
 *   "holderName": "Neha",
 *   "balance": 5000,
 *   "status": "ACTIVE"
 * }
 *
 * 2. Get All Accounts
 * GET http://localhost:8080/accounts
 *
 * 3. Pagination
 * GET http://localhost:8080/accounts?page=0&size=5
 *
 * 4. Sorting Ascending
 * GET http://localhost:8080/accounts?sort=balance,asc
 *
 * 5. Sorting Descending
 * GET http://localhost:8080/accounts?sort=balance,desc
 *
 * 6. Filter By Status
 * GET http://localhost:8080/accounts?status=ACTIVE
 *
 * 7. Search By Holder Name
 * GET http://localhost:8080/accounts?search=Sahil
 *
 * 8. Pagination + Sorting + Filtering + Searching
 * GET http://localhost:8080/accounts?page=0&size=5&sort=balance,asc&status=ACTIVE&search=Sahil
 *
 * =====================================================
 */