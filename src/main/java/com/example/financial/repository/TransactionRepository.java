
package com.example.financial.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.financial.entity.Account;
import com.example.financial.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ADMIN - Global Search
    // Searches:
    // username
    // transaction type
    // status
    // account number
    // account ID
    // transaction date/time

    @Query("""
        SELECT t
        FROM Transaction t, Account a
        WHERE t.accountId = a.id
        AND (
            LOWER(COALESCE(t.username, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(t.type, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(t.status, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(a.accountNumber, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR STR(t.accountId) LIKE CONCAT('%', :search, '%')
            OR STR(t.transactionDateTime) LIKE CONCAT('%', :search, '%')
        )
    """)
    Page<Transaction> globalSearch(
            String search,
            Pageable pageable);


    Optional<Transaction> findByIdAndAccountId(
            Long id,
            Long accountId);


    // USER - Get transactions of user's accounts
    Page<Transaction> findByAccountIdIn(
            java.util.List<Long> accountIds,
            Pageable pageable);


    Page<Transaction> findByAccountIdInAndType(
            java.util.List<Long> accountIds,
            String type,
            Pageable pageable);


    Page<Transaction> findByAccountIdInAndStatus(
            java.util.List<Long> accountIds,
            String status,
            Pageable pageable);


    Page<Transaction> findByAccountIdInAndTypeContainingIgnoreCase(
            java.util.List<Long> accountIds,
            String type,
            Pageable pageable);


    // ADMIN - Filtering by type
    Page<Transaction> findByType(
            String type,
            Pageable pageable);


    // ADMIN - Filtering by status
    Page<Transaction> findByStatus(
            String status,
            Pageable pageable);


    // ADMIN - Filtering by accountId
    Page<Transaction> findByAccountId(
            Long accountId,
            Pageable pageable);


    // ADMIN - Searching by type
    Page<Transaction> findByTypeContainingIgnoreCase(
            String type,
            Pageable pageable);


    // ADMIN - Searching by status
    Page<Transaction> findByStatusContainingIgnoreCase(
            String status,
            Pageable pageable);
    /*
     * ============================================================
     *              AUTH API TESTING
     * ============================================================
     *
     * 1. Register User
     * USER → No JWT required
     *
     * POST http://localhost:8080/register
     *
     * Body:
     * {
     *   "username": "rahul",
     *   "password": "rahul123"
     * }
     *
     *
     * 2. Login User
     * USER → No JWT required
     *
     * POST http://localhost:8080/login
     *
     * Body:
     * {
     *   "username": "rahul",
     *   "password": "rahul123"
     * }
     *
     * Login response se JWT Token milega.
     *
     * Protected APIs ke liye:
     * Authorization → Bearer Token
     *
     *
     * ============================================================
     *              ACCOUNT API TESTING
     * ============================================================
     *
     * 3. Create Account
     * USER → Apne JWT Token ke saath
     *
     * POST http://localhost:8080/createAccount
     *
     * Body:
     * {
     *   "accountNumber": "ACC200",
     *   "holderName": "Rahul",
     *   "balance": 5000,
     *   "status": "ACTIVE"
     * }
     *
     *
     * 4. Get Accounts
     *
     * USER → Sirf apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts
     *
     *
     * 5. Pagination
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?page=0&size=5
     *
     *
     * 6. Sorting Ascending
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?sort=balance,asc
     *
     *
     * 7. Sorting Descending
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?sort=balance,desc
     *
     *
     * 8. Filter By Status
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?status=ACTIVE
     *
     *
     * 9. Search By Holder Name
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?search=Rahul
     *
     *
     * 10. Pagination + Sorting + Filtering + Searching
     *
     * USER → Apne accounts
     * ADMIN → Saare accounts
     *
     * GET http://localhost:8080/accounts?page=0&size=5&sort=balance,asc&status=ACTIVE&search=Rahul
     *
     *
     * ============================================================
     *              TRANSACTION API TESTING
     * ============================================================
     *
     * 11. Deposit
     * USER → Sirf apne account me
     *
     * POST http://localhost:8080/deposit?accountId=5&amount=1000
     *
     *
     * 12. Withdrawal
     * USER → Sirf apne account se
     *
     * POST http://localhost:8080/withdrawal?accountId=5&amount=500
     *
     *
     * 13. Transfer
     * USER → Apne account se transfer
     *
     * POST http://localhost:8080/transfer?senderId=5&receiverId=6&amount=1000
     *
     *
     * 14. Reversal
     * USER → Apni transaction
     *
     * POST http://localhost:8080/reversal?transactionId=1
     *
     *
     * ============================================================
     *              GET TRANSACTIONS
     * ============================================================
     *
     * 15. Get Transactions
     *
     * USER → Sirf apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions
     *
     *
     * 16. Pagination
     *
     * USER → Apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions?page=0&size=5
     *
     *
     * 17. Sorting Ascending
     *
     * USER → Apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions?sort=amount,asc
     *
     *
     * 18. Sorting Descending
     *
     * USER → Apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions?sort=amount,desc
     *
     *
     * 19. Filter By Type
     *
     * USER → Apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions?type=TRANSFER
     *
     *
     * 20. Filter By Status
     *
     * USER → Apni transactions
     * ADMIN → Saari transactions
     *
     * GET http://localhost:8080/transactions?status=SUCCESS
     *
     *
     * 21. Filter By Account ID
     *
     * USER → Sirf apna account ID
     * ADMIN → Kisi bhi account ID ki transactions
     *
     * GET http://localhost:8080/transactions?accountId=5
     *
     *
     * 22. Search
     *
     * USER → Apni transactions me search
     * ADMIN → Global transaction search
     *
     * GET http://localhost:8080/transactions?search=Deposit
     *
     *
     * ============================================================
     *              ADMIN TRANSACTION SEARCH
     * ============================================================
     *
     * ADMIN → Same /transactions API se search karega.
     *
     * 23. Search By Transaction Type
     *
     * GET http://localhost:8080/transactions?search=Deposit
     *
     *
     * 24. Search By Username
     *
     * GET http://localhost:8080/transactions?search=rahul
     *
     *
     * 25. Search By Account Number
     *
     * GET http://localhost:8080/transactions?search=ACC200
     *
     *
     * 26. Search By Account ID
     *
     * GET http://localhost:8080/transactions?search=5
     *
     *
     * 27. Search By Status
     *
     * GET http://localhost:8080/transactions?search=SUCCESS
     *
     *
     * 28. Search By Month
     *
     * GET http://localhost:8080/transactions?search=2026-09
     *
     *
     * 29. Global Search + Pagination + Sorting
     *
     * GET http://localhost:8080/transactions?search=Deposit&page=0&size=5&sort=amount,desc
     *
     *
     * ============================================================
     *              AUDIT LOG API TESTING
     * ============================================================
     *
     * 30. Get Audit Logs
     *
     * USER → Sirf apne audit logs
     * ADMIN → Saare audit logs
     *
     * GET http://localhost:8080/audit-logs
     *
     *
     * ============================================================
     *              SECURITY TESTING
     * ============================================================
     *
     * 31. USER Accessing Another User's Account
     *
     * USER → Doosre USER ke account par operation
     *
     * POST http://localhost:8080/deposit?accountId=OTHER_USER_ACCOUNT_ID&amount=100
     *
     * Expected:
     * Access denied / Account not found
     *
     *
     * 32. Without JWT Token
     *
     * NO TOKEN
     *
     * GET http://localhost:8080/accounts
     *
     * Expected:
     * 401 Unauthorized
     *
     *
     * ============================================================
     *              NEGATIVE TESTING
     * ============================================================
     *
     * 33. Negative Deposit
     *
     * POST http://localhost:8080/deposit?accountId=5&amount=-100
     *
     * Expected:
     * Error
     *
     *
     * 34. Zero Deposit
     *
     * POST http://localhost:8080/deposit?accountId=5&amount=0
     *
     * Expected:
     * Error
     *
     *
     * 35. Negative Withdrawal
     *
     * POST http://localhost:8080/withdrawal?accountId=5&amount=-100
     *
     * Expected:
     * Error
     *
     *
     * 36. Insufficient Balance
     *
     * POST http://localhost:8080/withdrawal?accountId=5&amount=999999999
     *
     * Expected:
     * Insufficient balance
     *
     *
     * 37. Invalid Account
     *
     * POST http://localhost:8080/deposit?accountId=99999&amount=100
     *
     * Expected:
     * Account not found / access denied
     *
     *
     * ============================================================
     */
 
}

