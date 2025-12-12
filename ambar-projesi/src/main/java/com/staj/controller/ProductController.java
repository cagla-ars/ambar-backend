package com.staj.controller;

import com.staj.dto.ProductDTO;
import com.staj.entity.Product;
import com.staj.entity.Transaction;
import com.staj.entity.TransactionType;
import com.staj.entity.User;
import com.staj.entity.Role;
import com.staj.repository.ProductRepository;
import com.staj.repository.UserRepository;
import com.staj.service.TransactionService;
import com.staj.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.stream.Collectors;
import com.staj.dto.ProductCreateDTO;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")

public class ProductController {

    private final ProductRepository productRepository;      
    private final TransactionService transactionService;     
    private final UserRepository userRepository;             
    private final ProductService productService;             

    public ProductController(ProductRepository productRepository,
                             TransactionService transactionService,
                             UserRepository userRepository,ProductService productService) {
        this.productRepository = productRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.productService = productService;
    }

@GetMapping
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public List<ProductDTO> getAllProducts(Authentication authentication) {


    User currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

    List<Product> products;

        if (currentUser.getRole() == Role.USER) {
            products = productService.getActiveProducts(currentUser.getId());
        } else {
            products = productRepository.findByDeletedFalse();
        }

    return products.stream()
            .map(p -> new ProductDTO(
                    p.getId(),
                    p.getName(),
                    p.getBarcode(),
                    p.getCategory(),
                    p.getUnit(),
                    p.getUnitPrice(),
                    p.getStockQuantity()
            ))
            .collect(Collectors.toList());
}

@PostMapping
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public ResponseEntity<?> createProduct(
        @Valid @RequestBody ProductCreateDTO dto,
        Authentication authentication,
        @RequestParam(required = false) String type) {

    User currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

    if (productRepository.findByBarcodeAndUserId(dto.getBarcode(), currentUser.getId()).isPresent()) {
        throw new RuntimeException("Aynı barkoda sahip ürün zaten mevcut");
    }

    Product product = new Product();
    product.setName(dto.getName());
    product.setBarcode(dto.getBarcode());
    product.setCategory(dto.getCategory());
    product.setUnit(dto.getUnit());
    product.setUnitPrice(dto.getUnitPrice());
    product.setStockQuantity(dto.getStockQuantity());
    product.setUser(currentUser);

    Product saved = productRepository.save(product);

    Transaction tx = new Transaction();
    tx.setType(type != null ? TransactionType.valueOf(type.toUpperCase()) : TransactionType.ADD);
    tx.setQuantity(saved.getStockQuantity());
    tx.setProduct(saved);
    tx.setUser(currentUser);

    transactionService.createTransaction(tx);

    return ResponseEntity.ok(new ProductDTO(saved));
}


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody Product updated,
                                           Authentication authentication,
                                           @RequestParam(required = false) String type) {

            if (id == null) {
                throw new RuntimeException("ID null olamaz");
            }

            Product existing = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));


            if (currentUser.getRole() == Role.USER && !existing.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Bu ürünü güncelleme yetkiniz yok");
            }


            if (updated.getName() != null && !updated.getName().isBlank())
                existing.setName(updated.getName());
            if (updated.getBarcode() != null)
                existing.setBarcode(updated.getBarcode());
            if (updated.getCategory() != null)
                existing.setCategory(updated.getCategory());
            if (updated.getUnit() != null)
                existing.setUnit(updated.getUnit());
            if (updated.getUnitPrice() != null && updated.getUnitPrice() >= 0)
                existing.setUnitPrice(updated.getUnitPrice());
            if (updated.getStockQuantity() != null && updated.getStockQuantity() >= 0)
                existing.setStockQuantity(updated.getStockQuantity());

            Product saved = productRepository.save(existing);

            Transaction tx = new Transaction();
            tx.setType(type != null ? TransactionType.valueOf(type.toUpperCase()) : TransactionType.UPDATE);
            tx.setQuantity(updated.getStockQuantity() != null ? updated.getStockQuantity() : saved.getStockQuantity());
            tx.setProduct(saved);
            tx.setUser(currentUser);

            transactionService.createTransaction(tx);

            return ResponseEntity.ok(new ProductDTO(
                    saved.getId(),
                    saved.getName(),
                    saved.getBarcode(),
                    saved.getCategory(),
                    saved.getUnit(),
                    saved.getUnitPrice(),
                    saved.getStockQuantity()
            ));


    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           Authentication authentication,
                                           @RequestParam(required = false) String type) {

            if (id == null) {
    throw new RuntimeException("ID null olamaz");
}

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));

            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));


            if (currentUser.getRole() == Role.USER && !product.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Bu ürünü silme yetkiniz yok");
            }

            Transaction tx = new Transaction();
            tx.setType(type != null ? TransactionType.valueOf(type.toUpperCase()) : TransactionType.REMOVE);
            tx.setQuantity(product.getStockQuantity());
            tx.setProduct(product);
            tx.setUser(currentUser);

            transactionService.createTransaction(tx);

            productService.deleteProduct(id, tx);


            return ResponseEntity.ok("Ürün başarıyla silindi");
    }

@GetMapping("/search")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public List<ProductDTO> searchProducts(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String barcode,
        @RequestParam(required = false) String category,
        Authentication authentication
) {


    User currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

    List<Product> products;

    if (barcode != null && !barcode.isBlank()) {
        return productRepository.findByBarcodeAndUserId(barcode, currentUser.getId())
                .map(p -> List.of(new ProductDTO(
                        p.getId(), p.getName(), p.getBarcode(),
                        p.getCategory(), p.getUnit(),
                        p.getUnitPrice(), p.getStockQuantity()
                )))
                .orElse(List.of());
    }

    if (name != null && !name.isBlank()) {
        products = productRepository.findByUserIdAndNameContainingIgnoreCase(currentUser.getId(), name);

    } else if (category != null && !category.isBlank()) {
        products = productRepository.findByUserIdAndCategoryIgnoreCase(currentUser.getId(), category);

    } else {
        products = productRepository.findByUserIdAndDeletedFalse(currentUser.getId());
    }

    return products.stream()
            .map(p -> new ProductDTO(
                    p.getId(),
                    p.getName(),
                    p.getBarcode(),
                    p.getCategory(),
                    p.getUnit(),
                    p.getUnitPrice(),
                    p.getStockQuantity()
            ))
            .collect(Collectors.toList());
}

}
