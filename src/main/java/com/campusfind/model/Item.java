package com.campusfind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Lost or Found item reported on campus.
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 50)
    private String category;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 2000, message = "Description must provide sufficient detail (5-2000 characters)")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 150, message = "Location must be between 2 and 150 characters")
    @Column(nullable = false, length = 150)
    private String location;

    @NotBlank(message = "Report type is required (LOST or FOUND)")
    @Column(nullable = false, length = 20)
    private String type; // LOST or FOUND

    @NotNull(message = "Date reported is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate dateReported;

    @NotBlank(message = "Status is required (PENDING or RECOVERED)")
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING or RECOVERED

    @Column(length = 200)
    private String contact;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Default constructor
    public Item() {
    }

    // Parameterized constructor
    public Item(String name, String category, String description, String location,
                String type, LocalDate dateReported, String status, String contact) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.location = location;
        this.type = (type != null) ? type.toUpperCase() : "LOST";
        this.dateReported = (dateReported != null) ? dateReported : LocalDate.now();
        this.status = (status != null) ? status.toUpperCase() : "PENDING";
        this.contact = contact;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.type != null) {
            this.type = this.type.toUpperCase();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "PENDING";
        } else {
            this.status = this.status.toUpperCase();
        }
        if (this.dateReported == null) {
            this.dateReported = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.type != null) {
            this.type = this.type.toUpperCase();
        }
        if (this.status != null) {
            this.status = this.status.toUpperCase();
        }
    }

    // Convenience helper methods for Thymeleaf UI
    public boolean isLost() {
        return "LOST".equalsIgnoreCase(this.type);
    }

    public boolean isFound() {
        return "FOUND".equalsIgnoreCase(this.type);
    }

    public boolean isRecovered() {
        return "RECOVERED".equalsIgnoreCase(this.status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    // Get Bootstrap / UI icon according to category
    public String getCategoryIcon() {
        if (category == null) return "bi-box";
        switch (category.toLowerCase()) {
            case "electronics":
                return "bi-laptop";
            case "documents":
                return "bi-file-earmark-person";
            case "accessories":
                return "bi-watch";
            case "books":
                return "bi-book";
            case "clothing":
                return "bi-bag";
            case "personal belongings":
                return "bi-wallet2";
            default:
                return "bi-tag";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = (type != null) ? type.toUpperCase() : null;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public void setDateReported(LocalDate dateReported) {
        this.dateReported = dateReported;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null) ? status.toUpperCase() : "PENDING";
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
