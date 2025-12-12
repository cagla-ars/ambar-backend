package com.staj.service;

import com.staj.entity.Product;
import com.staj.entity.Transaction;
import com.staj.repository.ProductRepository;
import com.staj.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public ProductService(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }


    public Product createProduct(Product product, Transaction transaction) {

        if (product.getCategory() == null || product.getCategory().isEmpty())
            product.setCategory("Genel");
        if (product.getUnit() == null || product.getUnit().isEmpty())
            product.setUnit("Adet");

        Optional<Product> existing = productRepository.findByBarcode(product.getBarcode());
        if (existing.isPresent()) {
            throw new RuntimeException("Aynı barcode ile başka bir ürün zaten mevcut!");
        }


        Product savedProduct = productRepository.save(product);


        transaction.setQuantity(savedProduct.getStockQuantity());
        transaction.setProduct(savedProduct);
        transactionRepository.save(transaction);

        return savedProduct;
    }


    public Product updateProduct(Long id, Product updatedProduct, Transaction transaction) {
        if (id == null) {
        throw new IllegalArgumentException("ID null olamaz");
    }
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        if (!existing.getBarcode().equals(updatedProduct.getBarcode())) {
            Optional<Product> barcodeCheck = productRepository.findByBarcode(updatedProduct.getBarcode());
            if (barcodeCheck.isPresent()) {
                throw new RuntimeException("Aynı barcode ile başka bir ürün zaten mevcut!");
            }
        }


        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory() != null ? updatedProduct.getCategory() : "Genel");
        existing.setUnit(updatedProduct.getUnit() != null ? updatedProduct.getUnit() : "Adet");
        existing.setUnitPrice(updatedProduct.getUnitPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setBarcode(updatedProduct.getBarcode());

        Product savedProduct = productRepository.save(existing);

        transaction.setQuantity(updatedProduct.getStockQuantity());
        transaction.setProduct(savedProduct);
        transactionRepository.save(transaction);

        return savedProduct;
    }

    public void deleteProduct(Long id, Transaction transaction) {
        if (id == null) {
        throw new IllegalArgumentException("ID null olamaz");
    }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

        transaction.setQuantity(product.getStockQuantity());
        transaction.setProduct(product);
        transactionRepository.save(transaction);


        product.setDeleted(true);
        productRepository.save(product);
    }
    

    public List<Product> getActiveProducts(Long userId) {
        return productRepository.findByUserIdAndDeletedFalse(userId);
    }

    
}

