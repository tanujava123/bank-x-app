package com.banking.app.service;

import com.banking.app.model.*;

import java.util.List;

public interface BankService {

     GenericResponse registerCustomer(RegisterCustomer registerCustomer);

     GenericResponse createTransaction(TransactionDetails transactionDetails);

     GenericResponse fetchTransactionHistory(String phoneNumber);

     GenericResponse createBulkTransaction(List<TransactionDetails> transactionDetailsList);


}
