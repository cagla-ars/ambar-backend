package com.staj.repository;

import com.staj.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t JOIN FETCH t.user JOIN FETCH t.product")
    List<Transaction> findAllWithUserAndProduct();

    @Query("SELECT t FROM Transaction t JOIN FETCH t.user JOIN FETCH t.product WHERE t.id = :id")
    Transaction findByIdWithUserAndProduct(@Param("id") Long id);
}

