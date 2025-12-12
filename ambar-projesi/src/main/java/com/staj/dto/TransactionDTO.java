package com.staj.dto;

import java.time.LocalDateTime;

import com.staj.entity.TransactionType;

public class TransactionDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String username;
    private TransactionType type;
    private Integer quantity;
    private LocalDateTime createdAt;

    public TransactionDTO() {}

    public TransactionDTO(Long id, Long productId, String productName,
                          Long userId, String username,
                          TransactionType type, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.userId = userId;
        this.username = username;
        this.type = type;
        this.quantity = quantity;
        
    }

    public TransactionDTO(com.staj.entity.Transaction t) {
    this.id = t.getId();
    this.productId = t.getProduct() != null ? t.getProduct().getId() : null;
    this.productName = t.getProduct() != null ? t.getProduct().getName() : "Bilinmiyor";
    this.userId = t.getUser() != null ? t.getUser().getId() : null;
    this.username = t.getUser() != null ? t.getUser().getUsername() : "Bilinmiyor";
    this.type = t.getType();
    this.quantity = t.getQuantity();
    this.createdAt = t.getCreatedAt();
}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
