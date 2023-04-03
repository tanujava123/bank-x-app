package com.banking.app.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="customer_info")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<BankAccount> bankAccount = new ArrayList<>();
}
