package com.banking.app.service;

import com.banking.app.model.*;

public interface BankService {

     GenericResponse registerCustomer(RegisterCustomer registerCustomer);

     GenericResponse createTransaction(TransactionDetails transactionDetails);

     GenericResponse fetchTransactionHistory(String phoneNumber);


}
