package com.fashion.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String itemName;

    private String material;

    private BigDecimal unitPrice;

    private int quantity;

    private String size;  // optional (e.g., S, M, L, XL)

    private String barcode;

    public Inventory() {

    }

    // ====== Helper Methods ======
    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public boolean isLowStock() {
        return quantity < 5;
    }

    // ========= Getters & Setters ==========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitprice) {
        this.unitPrice = unitprice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
