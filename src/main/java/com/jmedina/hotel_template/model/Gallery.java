package com.jmedina.hotel_template.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "gallery")
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    // URL que devuelve Cloudinary al subir la foto
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    // public_id de Cloudinary — Necesario para eliminar la foto desde la API
    @NotBlank(message = "El public_id de Cloudinary es obligatorio")
    @Column(name = "cloudinary_public_id", nullable = false, length = 300)
    private String cloudinaryPublicId;

    // Texto alternativo a la imagen
    @Size(max = 255)
    @Column(name = "alt_text", length = 255)
    private String altText;

    // Título o pie de foto visible en la galería del frontend
    @Size(max = 150)
    @Column(length = 150)
    private String caption;

    // Categoría para filtrar fotos en el frontend
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(length = 30)
    private ImageCategory category = ImageCategory.GENERAL;

    // Orden en que aparece la foto en la galería
    // el admin puede reordenar arrastrando en el panel
    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Solo las fotos activas se muestran en el frontend
    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;

    // Dimensiones de la imagen 
    // para reservar el espacio antes de que cargue (se evita el layout shift)
    @Column
    private Integer width;

    @Column
    private Integer height;

    // Tamaño del archivo en bytes — Cloudinary lo devuelve al subir
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------
    // RELACIONES
    // -------------------------------------------------------

    // La foto pertenece a un hotel
    @JsonBackReference("hotel-gallery")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // Una foto puede pertenecer opcionalmente a un tipo de habitación
    // si es null, es una foto general del hotel (lobby, piscina, restaurante...)
    // si tiene valor, es una foto específica de ese tipo de habitación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")  // nullable — no tiene nullable = false
    private RoomType roomType;

    // -------------------------------------------------------
    // LÓGICA AUTOMÁTICA
    // -------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ImageCategory {
        GENERAL,      // fachada, lobby, áreas comunes
        ROOM,         // habitaciones
        POOL,         // piscina
        RESTAURANT,   // restaurante / bar
        SPA,          // spa y bienestar
        EXTERIOR,     // jardines, estacionamiento, vista aérea
        AMENITIES     // gimnasio, sala de reuniones, etc.
    }
}
