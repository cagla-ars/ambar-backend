package com.staj.dto;

import com.staj.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String barcode;
    private String category;
    private String unit;
    private Double unitPrice;
    private Integer stockQuantity;


    public ProductDTO(Long id, String name, String barcode, String category,
                      String unit, Double unitPrice, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }

    public ProductDTO(Product product) {
        this(product.getId(), product.getName(), product.getBarcode(), product.getCategory(),
             product.getUnit(), product.getUnitPrice(), product.getStockQuantity());
    }
}
