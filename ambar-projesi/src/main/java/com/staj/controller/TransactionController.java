package com.staj.controller;

import com.staj.dto.TransactionDTO;
import com.staj.entity.Transaction;
import com.staj.entity.TransactionType;
import com.staj.entity.User;
import com.staj.repository.UserRepository;
import com.staj.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions()
                .stream()
                .map(TransactionDTO::new) 
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionDTO getTransactionById(@PathVariable Long id) {
        Transaction t = transactionService.getTransactionById(id);
        return new TransactionDTO(t);
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody Transaction transaction,
                                            @RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) String type) {


        if (userId != null) {
            User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
            transaction.setUser(user);
        }

        if (type != null) {
            try {
                transaction.setType(TransactionType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Geçersiz transaction tipi");
            }
        }

        if (transaction.getType() == null) {
            throw new RuntimeException("Transaction type zorunludur");
        }

        Transaction saved = transactionService.createTransaction(transaction);

        return new TransactionDTO(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }
}
