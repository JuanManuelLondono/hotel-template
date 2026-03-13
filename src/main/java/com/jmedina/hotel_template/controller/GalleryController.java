package com.jmedina.hotel_template.controller;

import com.jmedina.hotel_template.dto.response.ApiResponseDTO;
import com.jmedina.hotel_template.dto.response.GalleryResponseDTO;
import com.jmedina.hotel_template.service.CloudinaryService;
import com.jmedina.hotel_template.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;
    private final CloudinaryService cloudinaryService;

    // GET /api/gallery/hotel/{hotelId} — público
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponseDTO<List<GalleryResponseDTO>>> getByHotel(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(galleryService.findByHotel(hotelId), "Galería obtenida")
        );
    }

    // GET /api/gallery/hotel/{hotelId}/category/{category} — público
    @GetMapping("/hotel/{hotelId}/category/{category}")
    public ResponseEntity<ApiResponseDTO<List<GalleryResponseDTO>>> getByCategory(
            @PathVariable Long hotelId,
            @PathVariable String category) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(
                galleryService.findByHotelAndCategory(hotelId, category),
                "Imágenes por categoría obtenidas"
            )
        );
    }

    // GET /api/gallery/room-type/{roomTypeId} — público
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<ApiResponseDTO<List<GalleryResponseDTO>>> getByRoomType(
            @PathVariable Long roomTypeId) {
        return ResponseEntity.ok(
            ApiResponseDTO.ok(galleryService.findByRoomType(roomTypeId), "Imágenes obtenidas")
        );
    }

    // POST /api/gallery/hotel/{hotelId}/upload — solo ADMIN
    @PostMapping(value = "/hotel/{hotelId}/upload",
                consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<GalleryResponseDTO>> upload(
            @PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) String altText,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false, defaultValue = "GENERAL") String category,
            jakarta.servlet.http.HttpServletRequest request
    ) throws IOException {

        System.out.println("Content-Type: " + request.getContentType());
        
        // 1. Subir a Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, "hotels/" + hotelId);

        // 2. Extraer datos de la respuesta de Cloudinary
        String imageUrl    = (String) uploadResult.get("secure_url");
        String publicId    = (String) uploadResult.get("public_id");
        Integer width      = (Integer) uploadResult.get("width");
        Integer height     = (Integer) uploadResult.get("height");
        Long fileSize      = ((Number) uploadResult.get("bytes")).longValue();

        // 3. Guardar en BD
        GalleryResponseDTO saved = galleryService.save(
            hotelId, roomTypeId, imageUrl, publicId,
            altText, caption, category, width, height, fileSize
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponseDTO.ok(saved, "Imagen subida exitosamente"));
    }

    // DELETE /api/gallery/{id} — solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) throws IOException {
        // 1. Obtener el publicId y eliminar de BD
        String publicId = galleryService.deleteAndGetPublicId(id);
        // 2. Eliminar de Cloudinary
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(ApiResponseDTO.ok(null, "Imagen eliminada"));
    }
}