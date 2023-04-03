package com.banking.app.repository;

import com.banking.app.entity.BankAccount;
import com.banking.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<BankAccount, Long> {

    BankAccount findByAccountNumber(String accountNumber);

    List<BankAccount> findAllById(int customerId);

    List<BankAccount> findAllByCustomer(Customer customer);
}
