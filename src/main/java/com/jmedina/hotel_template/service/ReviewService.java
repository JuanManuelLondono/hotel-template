package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.ReviewRequestDTO;
import com.jmedina.hotel_template.dto.response.ReviewResponseDTO;
import com.jmedina.hotel_template.model.Hotel;
import com.jmedina.hotel_template.model.Review;
import com.jmedina.hotel_template.model.User;
import com.jmedina.hotel_template.repository.HotelRepository;
import com.jmedina.hotel_template.repository.ReservationRepository;
import com.jmedina.hotel_template.repository.ReviewRepository;
import com.jmedina.hotel_template.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final HotelService hotelService;

    // Reseñas visibles de un hotel
    public List<ReviewResponseDTO> findByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdAndVisibleTrueOrderByCreatedAtDesc(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Crear reseña
    @Transactional
    public ReviewResponseDTO create(Long userId, Long hotelId, ReviewRequestDTO dto) {

        // Verificar que el hotel existe
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado"));

        // Verificar que el usuario no haya reseñado ya este hotel
        if (reviewRepository.findByUserIdAndHotelId(userId, hotelId).isPresent()) {
            throw new IllegalStateException("Ya dejaste una reseña para este hotel");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Verificar si el usuario realmente se hospedó — para marcar como verificada
        boolean verifiedStay = reservationRepository
                .countStaysByUserAtHotel(userId, hotelId) > 0;

        Review review = Review.builder()
                .user(user)
                .hotel(hotel)
                .rating(dto.getRating())
                .ratingCleanliness(dto.getRatingCleanliness())
                .ratingComfort(dto.getRatingComfort())
                .ratingLocation(dto.getRatingLocation())
                .ratingService(dto.getRatingService())
                .ratingValue(dto.getRatingValue())
                .title(dto.getTitle())
                .comment(dto.getComment())
                .verifiedStay(verifiedStay)
                .build();

        Review saved = reviewRepository.save(review);

        // Recalcular el promedio del hotel después de guardar la reseña
        recalculateHotelRating(hotelId);

        return toResponseDTO(saved);
    }

    // El hotel responde a una reseña
    @Transactional
    public ReviewResponseDTO addHotelResponse(Long reviewId, String response) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada"));
        review.setHotelResponse(response);
        return toResponseDTO(reviewRepository.save(review));
    }

    // Ocultar reseña inapropiada — solo ADMIN
    @Transactional
    public void hideReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada"));
        review.setVisible(false);
        reviewRepository.save(review);

        // Recalcular promedio excluyendo la reseña ocultada
        recalculateHotelRating(review.getHotel().getId());
    }

    // Recalcula y actualiza el promedio del hotel
    private void recalculateHotelRating(Long hotelId) {
        Double average = reviewRepository.getAverageRatingByHotel(hotelId);
        Long total = reviewRepository.countByHotelIdAndVisibleTrue(hotelId);
        hotelService.updateAverageRating(hotelId, average, total);
    }

    public ReviewResponseDTO toResponseDTO(Review r) {
        return ReviewResponseDTO.builder()
                .id(r.getId())
                .userName(r.getUser().getName())
                .rating(r.getRating())
                .ratingCleanliness(r.getRatingCleanliness())
                .ratingComfort(r.getRatingComfort())
                .ratingLocation(r.getRatingLocation())
                .ratingService(r.getRatingService())
                .ratingValue(r.getRatingValue())
                .title(r.getTitle())
                .comment(r.getComment())
                .hotelResponse(r.getHotelResponse())
                .hotelResponseAt(r.getHotelResponseAt())
                .verifiedStay(r.isVerifiedStay())
                .helpfulCount(r.getHelpfulCount())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
