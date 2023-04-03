package com.banking.app.service;

import com.banking.app.dao.ApplicationDao;
import com.banking.app.entity.BankAccount;
import com.banking.app.entity.Customer;
import com.banking.app.entity.Transaction;
import com.banking.app.exception.AccountNotFound;
import com.banking.app.exception.GenericException;
import com.banking.app.exception.InsufficientFundException;
import com.banking.app.model.GenericResponse;
import com.banking.app.model.RegisterCustomer;
import com.banking.app.model.TransactionDetails;
import com.banking.app.model.TransactionHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BankServiceImpl implements BankService {


    @Autowired
    ApplicationDao applicationDao;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public GenericResponse registerCustomer(RegisterCustomer registerCustomer) {
        Customer existingCustomer = applicationDao.checkCustomerExist(registerCustomer.getPhoneNumber());
        if (!Objects.isNull(existingCustomer))
            return new GenericResponse("Duplicate Customer", 203, "Customer already exist", existingCustomer);

        Customer customer = applicationDao.registerCustomer(registerCustomer);
        BankAccount savingsAccount = applicationDao.createAccount(customer, "SAVINGS");
        BankAccount currentAccount = applicationDao.createAccount(customer, "CURRENT");
        return new GenericResponse("Successful", 200, "Customer registered successfully", customer);
    }

    @Override
    public GenericResponse createTransaction(TransactionDetails transaction) {
        try {
            Customer sender = applicationDao.checkCustomerExist(transaction.getSenderPhoneNumber());
            Customer receiver = applicationDao.checkCustomerExist(transaction.getReceiverPhoneNumber());
            if (Objects.isNull(sender) || Objects.isNull(receiver))
                throw new AccountNotFound("Customer with this phone number doesn't exist");

            if (transaction.getFromAccountType().equalsIgnoreCase("savings")) {
                if (!(transaction.getSenderPhoneNumber().equals(transaction.getReceiverPhoneNumber()))) {
                    return new GenericResponse("Failed", 204, "Saving Account cannot transfer in other accounts", null);
                }
            }
            BankAccount sendersBankAccount = applicationDao.fetchCustomerAccount(transaction.getSenderPhoneNumber(),
                    transaction.getFromAccountType());
            BankAccount receiverBankAccount = applicationDao.fetchCustomerAccount(transaction.getReceiverPhoneNumber(),
                    transaction.getToAccountType());

            Transaction newTransaction = applicationDao.createTransaction(transaction, "InProgress");

            List<BankAccount> lockedAccounts = new ArrayList<>();
            lockedAccounts.add(sendersBankAccount);
            lockedAccounts.add(receiverBankAccount);
            createVouchers(lockedAccounts, transaction);
            applicationDao.updateTransactionStatus(newTransaction, "Success");
            sendEmail(sender.getEmail());
            sendEmail(receiver.getEmail());
            return new GenericResponse("Successful", 200, "Balance Transfer success", lockedAccounts);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericException();
        }
    }

    public void sendEmail(String toEmail) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject("Transaction access is provided");
            simpleMailMessage.setText("Customer can do transaction now");
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {}
    }

    public void createVouchers(List<BankAccount> bankAccounts, TransactionDetails transactionDetails) {

        double senderBalance = bankAccounts.get(0).getAccountBalance();
        double receiverBalance = bankAccounts.get(1).getAccountBalance();
        double txnAmount = transactionDetails.getTxnAmt();
        String receiverAccountType = bankAccounts.get(1).getAccountType();
        double interest = 0;
        if (senderBalance >= txnAmount) {
            double charges = (senderBalance * .05) / 100;
            double senderTempBalance = senderBalance - txnAmount - charges;
            bankAccounts.get(0).setAccountBalance(senderTempBalance);
            if (receiverAccountType.equalsIgnoreCase("savings")) {
                interest = (receiverBalance * 0.5) / 100;
            }
            double receiverTempBalance = receiverBalance + txnAmount + interest;
            bankAccounts.get(1).setAccountBalance(receiverTempBalance);
        } else {
            throw new InsufficientFundException("Account has insufficient funds");
        }
        applicationDao.updateBalance(bankAccounts);
    }

    public GenericResponse fetchTransactionHistory(String phoneNumber) {
        List<Transaction> history = applicationDao.fetchTransactionHistory(phoneNumber);
        return new GenericResponse("Successful", 200, "All transactions", history);
    }


}
