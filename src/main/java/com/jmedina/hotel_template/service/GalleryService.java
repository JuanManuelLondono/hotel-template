package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.response.GalleryResponseDTO;
import com.jmedina.hotel_template.model.Gallery;
import com.jmedina.hotel_template.model.Gallery.ImageCategory;
import com.jmedina.hotel_template.model.Hotel;
import com.jmedina.hotel_template.model.RoomType;
import com.jmedina.hotel_template.repository.Galleryrepository;
import com.jmedina.hotel_template.repository.HotelRepository;
import com.jmedina.hotel_template.repository.RoomTypeRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final Galleryrepository galleryRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;

    // Todas las fotos activas de un hotel
    public List<GalleryResponseDTO> findByHotel(Long hotelId) {
        return galleryRepository.findByHotelIdAndActiveTrueOrderByDisplayOrderAsc(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Fotos por categoría
    public List<GalleryResponseDTO> findByHotelAndCategory(Long hotelId, String category) {
        ImageCategory imageCategory = ImageCategory.valueOf(category.toUpperCase());
        return galleryRepository.findByHotelIdAndCategoryAndActiveTrueOrderByDisplayOrderAsc(
                hotelId, imageCategory)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Fotos de un tipo de habitación
    public List<GalleryResponseDTO> findByRoomType(Long roomTypeId) {
        return galleryRepository.findByRoomType_IdAndActiveTrueOrderByDisplayOrderAsc(roomTypeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Guardar foto — llamado desde CloudinaryService después de subir
    @Transactional
    public GalleryResponseDTO save(Long hotelId, Long roomTypeId, String imageUrl,
                                   String publicId, String altText, String caption,
                                   String category, Integer width, Integer height, Long fileSize) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado"));

        RoomType roomType = null;
        if (roomTypeId != null) {
            roomType = roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de habitación no encontrado"));
        }

        Gallery gallery = Gallery.builder()
                .hotel(hotel)
                .roomType(roomType)
                .imageUrl(imageUrl)
                .cloudinaryPublicId(publicId)
                .altText(altText)
                .caption(caption)
                .category(category != null ? ImageCategory.valueOf(category.toUpperCase()) : ImageCategory.GENERAL)
                .width(width)
                .height(height)
                .fileSize(fileSize)
                .build();

        return toResponseDTO(galleryRepository.save(gallery));
    }

    // Eliminar foto — borra de BD, Cloudinary se maneja en CloudinaryService
    @Transactional
    public String deleteAndGetPublicId(Long imageId) {
        Gallery gallery = galleryRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Imagen no encontrada"));
        String publicId = gallery.getCloudinaryPublicId();
        galleryRepository.delete(gallery);
        return publicId; // el controller usará esto para borrar de Cloudinary
    }

    public GalleryResponseDTO toResponseDTO(Gallery g) {
        return GalleryResponseDTO.builder()
                .id(g.getId())
                .imageUrl(g.getImageUrl())
                .altText(g.getAltText())
                .caption(g.getCaption())
                .category(g.getCategory().name())
                .displayOrder(g.getDisplayOrder())
                .width(g.getWidth())
                .height(g.getHeight())
                .build();
    }
}
