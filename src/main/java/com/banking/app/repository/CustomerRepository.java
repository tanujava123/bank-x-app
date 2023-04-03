package com.banking.app.repository;

import com.banking.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

     Customer findById(int id);

     Customer findByPhoneNumber(String phoneNumber);
}
