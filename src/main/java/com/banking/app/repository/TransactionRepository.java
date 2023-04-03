package com.banking.app.repository;

import com.banking.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

     Transaction findById(long id);

    List<Transaction> findAllBySendersAccount(String account);

    List<Transaction> findAllByReceiversAccount(String account);

}
