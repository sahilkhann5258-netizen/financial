package com.example.financial.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.financial.entity.AuditLog;
import com.example.financial.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String username, String action, Long transactionId) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setTransactionId(transactionId);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}