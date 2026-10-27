package com.example.financial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financial.entity.Ledger;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
}