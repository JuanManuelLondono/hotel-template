package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.AmenityRequestDTO;
import com.jmedina.hotel_template.dto.response.AmenityResponseDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    // GET /api/amenities — público
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AmenityResponseDTO>>> findAll() {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(amenityService.findAll(), "Amenities obtenidos")
        );
    }

    // POST /api/amenities — solo ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<AmenityResponseDTO>> create(
            @Valid @RequestBody AmenityRequestDTO dto) {

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(amenityService.create(dto), "Amenity creado"));
    }

    // PUT /api/amenities/{id} — solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<AmenityResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody AmenityRequestDTO dto) {

        return ResponseEntity.ok(
            ApiResponseDTO.ok(amenityService.update(id, dto), "Amenity actualizado")
        );
    }

    // DELETE /api/amenities/{id} — solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        amenityService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.ok(null, "Amenity eliminado"));
    }
}