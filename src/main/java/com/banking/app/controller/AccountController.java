package com.banking.app.controller;

import com.banking.app.model.*;
import com.banking.app.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @Autowired
    BankService bankService;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerCustomer(@Validated @RequestBody RegisterCustomer registerCustomer) {
        return new ResponseEntity<>(bankService.registerCustomer(registerCustomer), HttpStatus.ACCEPTED);
    }

    @PostMapping("/transfer")
    public ResponseEntity<GenericResponse> balanceTransfer(@Validated @RequestBody TransactionDetails transactionDetails) {
        return new ResponseEntity<>(bankService.createTransaction(transactionDetails), HttpStatus.ACCEPTED);
    }

    @GetMapping("/history")
    public ResponseEntity<GenericResponse> txnHistory(@Validated @RequestParam("phoneNumber")String  phoneNumber) {
        return new ResponseEntity<>(bankService.fetchTransactionHistory(phoneNumber), HttpStatus.ACCEPTED);
    }
}
