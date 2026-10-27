package com.example.financial.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//IT WILL CREATE TABLE IN DATABASE...
@Entity
@Table(name = "accounts")
public class Account {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;
private String accountNumber;
private String holderName;
private BigDecimal balance;
private String status;
private String username;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getAccountNumber() {
	return accountNumber;
}
public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
}
public String getHolderName() {
	return holderName;
}
public void setHolderName(String holderName) {
	this.holderName = holderName;
}
public BigDecimal getBalance() {
	return balance;
}
public void setBalance(BigDecimal balance) {
	this.balance = balance;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getUsername() {
    return username;
}

public void setUsername(String username) {
    this.username = username;
}
}
