package com.fashion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_transactions")
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDateTime addDateTime;
    private String name;
    private String phoneNumber;
    private String email;
    private String typeOfCloth;
    private String color;
    private String material;
    private int quantity;
    private double purchasePrice;
    private double depositeAmount;
    private double remainingAmount;
    private LocalDateTime pickupDateTime;
    private String status;
    private String note;  // new Added field

    public CustomerTransaction() {}

    // ======== GETTERS ========

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDateTime getAddDateTime() { return addDateTime; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getTypeOfCloth() { return typeOfCloth; }
    public String getColor() { return color; }
    public String getMaterial() { return material; }
    public int getQuantity() { return quantity; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getDepositeAmount() { return depositeAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public LocalDateTime getPickupDateTime() { return pickupDateTime; }
    public String getStatus() { return status; }
    public String getNote() { return note; }  //new Getter

    // ======== SETTERS (FIX FOR ERROR) ========

    public void setUserId(Long userId) { this.userId = userId; }
    public void setAddDateTime(LocalDateTime addDateTime) { this.addDateTime = addDateTime; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setTypeOfCloth(String typeOfCloth) { this.typeOfCloth = typeOfCloth; }
    public void setColor(String color) { this.color = color; }
    public void setMaterial(String material) { this.material = material; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setDepositeAmount(double depositeAmount) { this.depositeAmount = depositeAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
    public void setPickupDateTime(LocalDateTime pickupDateTime) { this.pickupDateTime = pickupDateTime; }
    public void setStatus(String status) { this.status = status; }
    public void setNote(String note) { this.note = note; } 
}


