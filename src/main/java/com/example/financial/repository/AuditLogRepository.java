package com.example.financial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUsername(String username);
    
    
	/*
	 * ================= AUTH API TESTING =================
	 *
	 * 1. Register User
	 * POST http://localhost:8080/register
	 * Body:
	 * {
	 *   "username": "rahul",
	 *   "password": "rahul123"
	 * }
	 *
	 * 2. Login User
	 * POST http://localhost:8080/login
	 * Body:
	 * {
	 *   "username": "rahul",
	 *   "password": "rahul123"
	 * }
	 *
	 * Login response se JWT Token milega.
	 * Protected APIs ke liye Bearer Token use karo.
	 *
	 * ==================================================
	 */
}