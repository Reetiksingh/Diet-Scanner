package com.nutrilens.nutrition.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "foods", schema = "nutrition")
public class Food {
    @Id
    private UUID id;
    @Column(unique = true)
    private String barcode;
    @Column(name = "qr_code")
    private String qrCode;
    @Column(nullable = false)
    private String name;
    private String brand;
    @Column(nullable = false)
    private String source;
    @Column(name = "serving_size")
    private BigDecimal servingSize;
    @Column(name = "serving_unit")
    private String servingUnit;
    @Column(name = "created_by_user_id")
    private UUID createdByUserId;
    @Column(name = "created_at")
    private Instant createdAt;
    @OneToOne(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private FoodNutrition nutrition;

    protected Food() {
    }

    public Food(String barcode, String name, String brand, String source, BigDecimal servingSize, String servingUnit, UUID createdByUserId) {
        this.id = UUID.randomUUID();
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.source = source;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
        this.createdByUserId = createdByUserId;
        this.createdAt = Instant.now();
    }

    public void attachNutrition(FoodNutrition nutrition) {
        this.nutrition = nutrition;
        nutrition.attachFood(this);
    }

    public UUID getId() { return id; }
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getSource() { return source; }
    public BigDecimal getServingSize() { return servingSize; }
    public String getServingUnit() { return servingUnit; }
    public FoodNutrition getNutrition() { return nutrition; }
}

