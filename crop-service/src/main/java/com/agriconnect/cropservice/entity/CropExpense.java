package com.agriconnect.cropservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "expense_id")
    private String expenseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type")
    private ExpenseType expenseType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ExpenseType {
        SEED,
        FERTILIZER,
        PESTICIDE,
        IRRIGATION,
        LABOUR,
        EQUIPMENT,
        OTHER
    }
}

//
//**Key things to notice:**
//
//`CropStatus` has 7 stages — this represents the **real crop lifecycle** from planning to harvest. Each status change will trigger a Kafka event.
//
//`farmerId` is stored as plain String — crop-service doesn't join with farmer table. It just stores the ID. This is **microservice data isolation** — each service owns its own data.
//
//`CropExpense` tracks money spent on each crop — seeds, fertilizer, labour, pesticide. This gives farmers profit/loss analysis later.
//
//`@PrePersist` sets `cropStatus = PLANNED` automatically when crop is first created.
//
//---
//
//**Your entity layer:**
//```
//entity/
//├── Crop.java           ✅
//└── CropExpense.java    ✅