package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.request.RoomTypeRequestDTO;
import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.RoomTypeResponseDTO;
import com.jmedina.hotel_template.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    // GET /api/room-types/hotel/{hotelId} — público
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponseDTO<List<RoomTypeResponseDTO>>> findByHotel(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(roomTypeService.findByHotel(hotelId), "Tipos de habitación obtenidos")
        );
    }

    // GET /api/room-types/{id} — público
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RoomTypeResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(roomTypeService.findById(id), "Tipo de habitación obtenido")
        );
    }

    // POST /api/room-types/hotel/{hotelId} — solo ADMIN
    @PostMapping("/hotel/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoomTypeResponseDTO>> create(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomTypeRequestDTO dto) {

        RoomTypeResponseDTO created = roomTypeService.create(hotelId, dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(created, "Tipo de habitación creado"));
    }

    // PUT /api/room-types/{id} — solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RoomTypeResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequestDTO dto) {

        return ResponseEntity.ok(
            ApiResponseDTO.ok(roomTypeService.update(id, dto), "Tipo de habitación actualizado")
        );
    }
}