package com.banking.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {

    private String senderPhoneNumber;

    private String fromAccountType;

    private String receiverPhoneNumber;

    private String toAccountType;

    private double txnAmt;

    private long senderAccountNumber;

    private long receiverAccountNumber;
}

