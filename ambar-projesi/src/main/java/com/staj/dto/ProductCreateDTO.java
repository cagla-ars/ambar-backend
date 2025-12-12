package com.staj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductCreateDTO {

    @NotBlank(message = "Ürün adı boş olamaz")
    private String name;

    @NotBlank(message = "Barkod boş olamaz")
    private String barcode;

    private String category;
    private String unit;

    @NotNull(message = "Birim fiyat zorunludur")
    @PositiveOrZero(message = "Birim fiyat negatif olamaz")
    private Double unitPrice;

    @NotNull(message = "Stok miktarı zorunludur")
    @PositiveOrZero(message = "Stok miktarı negatif olamaz")
    private Integer stockQuantity;

    public ProductCreateDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
