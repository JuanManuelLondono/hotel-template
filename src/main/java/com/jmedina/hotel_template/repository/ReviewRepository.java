package com.jmedina.hotel_template.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    // Reseñas visibles de un hotel ordenadas por fecha — para el frontend
    List<Review> findByHotelIdAndVisibleTrueOrderByCreatedAtDesc(Long hotelId);

    // Reseña de un usuario en un hotel — para verificar si ya reseñó
    Optional<Review> findByUserIdAndHotelId(Long userId, Long hotelId);

    // Promedio de calificación de un hotel — para recalcular Hotel.averageRating
    @Query("SELECT AVG(r.rating) FROM Review r "
         + "WHERE r.hotel.id = :hotelId AND r.visible = true")
    Double getAverageRatingByHotel(@Param("hotelId") Long hotelId);

    // Total de reseñas visibles — para recalcular Hotel.totalReviews
    Long countByHotelIdAndVisibleTrue(Long hotelId);

    // Reseñas verificadas primero — mejor UX para el cliente
    List<Review> findByHotelIdAndVisibleTrueOrderByVerifiedStayDescCreatedAtDesc(Long hotelId);

}
