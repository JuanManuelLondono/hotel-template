package com.jmedina.hotel_template.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Gallery;
import com.jmedina.hotel_template.model.Gallery.ImageCategory;

@Repository
public interface Galleryrepository extends JpaRepository<Gallery, Long>{

    // Todas las fotos activas de un hotel ordenadas por displayOrder
    List<Gallery> findByHotelIdAndActiveTrueOrderByDisplayOrderAsc(Long hotelId);

    // Fotos de un hotel por categoría — para filtrar en la galería
    List<Gallery> findByHotelIdAndCategoryAndActiveTrueOrderByDisplayOrderAsc(
        Long hotelId,
        ImageCategory category
    );

    // Fotos de un tipo de habitación específico
    List<Gallery> findByRoomType_IdAndActiveTrueOrderByDisplayOrderAsc(Long roomTypeId);

    // Fotos generales del hotel (sin roomType asignado)
    List<Gallery> findByHotel_IdAndRoomTypeIsNullAndActiveTrueOrderByDisplayOrderAsc(Long hotelId);
}
