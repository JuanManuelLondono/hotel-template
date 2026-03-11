package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.ReviewRequestDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.ReviewResponseDTO;
import com.jmedina.hotel_template.service.ReviewService;
import com.jmedina.hotel_template.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    // GET /api/reviews/hotel/{hotelId} — público
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponseDTO<List<ReviewResponseDTO>>> getByHotel(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reviewService.findByHotel(hotelId), "Reseñas obtenidas")
        );
    }

    // POST /api/reviews/hotel/{hotelId} — usuario autenticado
    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponseDTO<ReviewResponseDTO>> create(
            @PathVariable Long hotelId,
            @Valid @RequestBody ReviewRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        ReviewResponseDTO created = reviewService.create(userId, hotelId, dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(created, "Reseña creada exitosamente"));
    }

    // POST /api/reviews/{id}/response — solo ADMIN responde una reseña
    @PostMapping("/{id}/response")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ReviewResponseDTO>> addResponse(
            @PathVariable Long id,
            @RequestParam String response) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(reviewService.addHotelResponse(id, response), "Respuesta agregada")
        );
    }

    // DELETE /api/reviews/{id} — solo ADMIN oculta reseña
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> hide(@PathVariable Long id) {
        reviewService.hideReview(id);
        return ResponseEntity.ok(ApiResponseDTO.ok(null, "Reseña ocultada"));
    }
}