package com.staj.repository;

import com.staj.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndUserId(Long productId, Long userId);

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByUserId(Long userId);

    List<Product> findAllByUserId(Long userId);

    List<Product> findByUserIdAndDeletedFalse(Long userId);

    Optional<Product> findByBarcodeAndUserId(String barcode, Long userId);

    List<Product> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);

    List<Product> findByUserIdAndCategoryIgnoreCase(Long userId, String category);

    List<Product> findByDeletedFalse();
}
