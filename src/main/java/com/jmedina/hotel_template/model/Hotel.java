package com.jmedina.hotel_template.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del hotel es obligatorio")
    @Size(min = 2, max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false, length = 255)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "El país es obligatorio")
    @Column(nullable = false, length = 100)
    private String country;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Longitud mínima es -90")
    @DecimalMax(value = "90.0", message = "Longitud máxima es 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud mínima es -180")
    @DecimalMax(value = "180.0", message = "Longitud máxima es 180")
    @Column(nullable = false)
    private Double longitude;

    @Pattern(regexp = "^\\+?[0-9\\-()]{7,20}$", message = "Número de teléfono inválido")
    @Column(length = 30)
    private String phone;

    @Email(message = "Email inválido")
    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String website; 

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Builder.Default
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "check_in_time", length = 10)
    private String checkInTime; // ej: "15:00" — se muestra en la página del hotel

    @Column(name = "check_out_time", length = 10)
    private String checkOutTime; // ej: "12:00"

    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy; // texto libre con la política del hotel

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------
    // RELACIONES
    // -------------------------------------------------------

    // Un hotel tiene muchos tipos de habitación
    @JsonManagedReference("hotel-roomtypes")  // controla la serialización en este lado
    @OneToMany(
        mappedBy = "hotel",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<RoomType> roomTypes;

    // Un hotel tiene muchas fotos en su galería
    @JsonManagedReference("hotel-gallery")
    @OneToMany(
        mappedBy = "hotel",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<Gallery> galleryImages;

    // Un hotel tiene muchas reseñas
    @JsonManagedReference("hotel-reviews")
    @OneToMany(
        mappedBy = "hotel",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<Review> reviews;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}
