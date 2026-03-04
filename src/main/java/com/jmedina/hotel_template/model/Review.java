package com.jmedina.hotel_template.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(
    name = "reviews",
    uniqueConstraints = {
        // Un usuario solo puede dejar UNA reseña por hotel
        @UniqueConstraint(columnNames = {"user_id", "hotel_id"})
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Calificación del 1 al 5
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer rating;

    // Calificaciones específicas por categoría — opcionales
    // permiten al cliente ser más detallado en su reseña
    @Min(1) @Max(5)
    @Column(name = "rating_cleanliness")
    private Integer ratingCleanliness; // limpieza

    @Min(1) @Max(5)
    @Column(name = "rating_comfort")
    private Integer ratingComfort; // comodidad

    @Min(1) @Max(5)
    @Column(name = "rating_location")
    private Integer ratingLocation; // ubicación

    @Min(1) @Max(5)
    @Column(name = "rating_service")
    private Integer ratingService; // servicio

    @Min(1) @Max(5)
    @Column(name = "rating_value")
    private Integer ratingValue; // relación calidad/precio

    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 2000, message = "El comentario debe tener entre 10 y 2000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    // Título corto de la reseña — opcional pero mejora la UX
    // ej: "Excelente estadía, lo recomiendo"
    @Size(max = 150)
    @Column(length = 150)
    private String title;

    // El hotel puede responder públicamente a la reseña
    @Column(name = "hotel_response", columnDefinition = "TEXT")
    private String hotelResponse;

    @Column(name = "hotel_response_at")
    private LocalDateTime hotelResponseAt; // fecha en que el hotel respondió

    // Indica si la reseña viene de alguien que realmente se hospedó
    // se marca true automáticamente si el usuario tiene una reserva CHECKED_OUT en este hotel
    @Builder.Default
    @Column(name = "is_verified_stay")
    private boolean isVerifiedStay = false;

    // Moderación — el admin puede ocultar reseñas inapropiadas sin eliminarlas
    @Builder.Default
    @Column(name = "is_visible")
    private boolean isVisible = true;

    // Cuántas personas encontraron útil esta reseña
    @Builder.Default
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------
    // RELACIONES
    // -------------------------------------------------------

    // La reseña pertenece a un usuario
    @JsonBackReference("user-reviews")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // La reseña pertenece a un hotel
    @JsonBackReference("hotel-reviews")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Si el hotel acaba de responder, se registra la fecha
        if (hotelResponse != null && hotelResponseAt == null) {
            hotelResponseAt = LocalDateTime.now();
        }
    }
}
