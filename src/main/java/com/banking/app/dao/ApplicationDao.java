package com.banking.app.dao;

import com.banking.app.entity.BankAccount;
import com.banking.app.entity.Customer;
import com.banking.app.entity.Transaction;
import com.banking.app.model.RegisterCustomer;
import com.banking.app.model.TransactionDetails;
import com.banking.app.repository.BankRepository;
import com.banking.app.repository.CustomerRepository;
import com.banking.app.repository.TransactionRepository;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ApplicationDao {

    @Autowired
    BankRepository bankRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TransactionRepository transactionRepository;

    AtomicLong atomicLong = new AtomicLong(810000L);

    AtomicLong atomicAccount = new AtomicLong(70000L);

    @Transactional
    public Customer registerCustomer(RegisterCustomer registerCustomer) {
        Customer customer = new Customer();
        customer.setId(atomicLong.getAndIncrement());
        customer.setCustomerName(registerCustomer.getCustomerName());
        customer.setEmail(registerCustomer.getEmail());
        customer.setPhoneNumber(registerCustomer.getPhoneNumber());
        customerRepository.save(customer);
        return customer;
    }

    @Transactional
    public BankAccount createAccount(Customer registerCustomer, String accountType) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(atomicAccount.getAndIncrement());
        bankAccount.setCustomerName(registerCustomer.getCustomerName());
        bankAccount.setAccountType(accountType);
        bankAccount.setCustomer(registerCustomer);
        if (accountType.equalsIgnoreCase("savings"))
            bankAccount.setAccountBalance(500.0);
        else
            bankAccount.setAccountBalance(0.0);
        bankRepository.save(bankAccount);
        return bankAccount;
    }


    public Customer checkCustomerExist(String customerPhoneNumber) {
        return customerRepository.findByPhoneNumber(customerPhoneNumber);
    }

    public BankAccount checkCustomerExist(long accountNumber) {
        return bankRepository.findByAccountNumber(accountNumber);
    }

    public BankAccount fetchCustomerAccount(String customerPhoneNumber, String accountType) {
        Customer customer = customerRepository.findByPhoneNumber(customerPhoneNumber);
        List<BankAccount> bankAccount = bankRepository.findAllByCustomer(customer);
        for (BankAccount acc : bankAccount) {
            if (acc.getAccountType().equalsIgnoreCase(accountType)) {
                return acc;
            }
        }
        return null;
    }

    public Transaction createTransaction(TransactionDetails txn, String status) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setSendersAccount(txn.getSenderPhoneNumber());
        transaction.setReceiversAccount(txn.getReceiverPhoneNumber());
        transaction.setTxnAmount(txn.getTxnAmt());
        transaction.setFromAccountType(txn.getFromAccountType());
        transaction.setToAccountType(txn.getToAccountType());
        transaction.setTxnDate(new Date());
        transaction.setStatus(status);
        transactionRepository.save(transaction);
        return transaction;
    }

    public void updateTransactionStatus(Transaction txn, String status) {
        Transaction transaction = transactionRepository.findById(txn.getId());
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    public List<BankAccount> lockedAccounts(List<BankAccount> bankAccounts) {
        List<BankAccount> lockedAccounts = new ArrayList<>();
        entityManager.getTransaction().begin();
        for (BankAccount acc : bankAccounts) {
            entityManager.find(BankAccount.class, acc.getId(), LockModeType.PESSIMISTIC_WRITE);
            lockedAccounts.add(acc);
        }
        return lockedAccounts;
    }

    public void updateBalance(List<BankAccount> bankAccounts) {
        BankAccount senderAccount = bankRepository.findById(bankAccounts.get(0).getId()).orElseThrow();
        senderAccount.setAccountBalance(bankAccounts.get(0).getAccountBalance());
        BankAccount receiverAccount = bankRepository.findById(bankAccounts.get(1).getId()).orElseThrow();
        receiverAccount.setAccountBalance(bankAccounts.get(1).getAccountBalance());
        bankRepository.save(senderAccount);
        bankRepository.save(receiverAccount);
    }

    public List<Transaction> fetchTransactionHistory(String phoneNumber) {
        return transactionRepository.findAllBySendersAccount(phoneNumber);
    }
}
