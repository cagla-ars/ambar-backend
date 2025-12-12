package com.staj.service;


import com.staj.entity.Transaction;

import com.staj.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction tx) {
        if (tx.getProduct() == null || tx.getProduct().getId() == null) {
            throw new RuntimeException("Geçersiz ürün");
        }
        if (tx.getType() == null) {
            throw new RuntimeException("Transaction type boş olamaz");
        }
        
        return transactionRepository.save(tx);
    }

   public List<Transaction> getAllTransactions() {
    return transactionRepository.findAllWithUserAndProduct();
}

public Transaction getTransactionById(Long id) {
    return transactionRepository.findByIdWithUserAndProduct(id);
}



    public void deleteTransaction(Long id) {
        if(id==null){
            throw new IllegalArgumentException("ID null olamaz");
        }
        transactionRepository.deleteById(id);
    }
}

