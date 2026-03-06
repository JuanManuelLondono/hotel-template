package com.jmedina.hotel_template.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long>{

     // Tipos de habitación activos de un hotel — para la página del hotel
    List<RoomType> findByHotelIdAndActiveTrue(Long hotelId);

    // Tipos ordenados por precio ascendente
    List<RoomType> findByHotelIdAndActiveTrueOrderByPricePerNightAsc(Long hotelId);

    // Tipos dentro de un rango de precio — para filtros del frontend
    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.id = :hotelId " +
            "AND rt.active = true " +
            "AND rt.pricePerNight BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY rt.pricePerNight ASC"
    )
    List<RoomType> findByHotel_IdAndPriceRange(
        @Param("hotelId") Long hotelId,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice
    );

    // Tipos con capacidad suficiente para X personas
    List<RoomType> findByHotel_IdAndCapacityGreaterThanEqualAndActiveTrue(
        @Param("hotelId") Long hotelId,
        @Param("capacity") Integer capacity
    );
}
