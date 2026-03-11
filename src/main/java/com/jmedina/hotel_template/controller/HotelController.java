package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.HotelRequestDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.HotelResponseDTO;
import com.jmedina.hotel_template.dto.response.HotelSummaryDTO;
import com.jmedina.hotel_template.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // GET /api/hotels — público
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<HotelSummaryDTO>>> findAll() {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(hotelService.findAllActive(), "Hoteles obtenidos")
        );
    }

    // GET /api/hotels/{id} — público
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<HotelResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(hotelService.findById(id), "Hotel obtenido")
        );
    }

    // GET /api/hotels/city/{city} — público
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponseDTO<List<HotelSummaryDTO>>> findByCity(
            @PathVariable String city) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(hotelService.findByCity(city), "Hoteles encontrados")
        );
    }

    // GET /api/hotels/top-rated — público
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponseDTO<List<HotelSummaryDTO>>> findTopRated() {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(hotelService.findTopRated(), "Hoteles mejor calificados")
        );
    }

    // POST /api/hotels — solo ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<HotelResponseDTO>> create(
            @Valid @RequestBody HotelRequestDTO dto) {

        HotelResponseDTO created = hotelService.create(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(created, "Hotel creado exitosamente"));
    }

    // PUT /api/hotels/{id} — solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<HotelResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequestDTO dto) {

        return ResponseEntity.ok(
            ApiResponseDTO.ok(hotelService.update(id, dto), "Hotel actualizado")
        );
    }

    // DELETE /api/hotels/{id} — solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deactivate(@PathVariable Long id) {
        hotelService.deactivate(id);
        return ResponseEntity.ok(ApiResponseDTO.ok(null, "Hotel desactivado"));
    }
}