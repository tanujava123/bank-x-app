package com.banking.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction_details")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "txn_id")
    private String transactionId;

    @Column(name = "txn_amount")
    private double txnAmount;

    @Column(name = "sender_account")
    private String sendersAccount;

    @Column(name = "receiver_account")
    private String receiversAccount;

    @Column(name = "fromAccountType")
    private String fromAccountType;

    @Column(name = "toAccountType")
    private String toAccountType;

    @Column(name = "status")
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "insert_date")
    private Date txnDate;

}
