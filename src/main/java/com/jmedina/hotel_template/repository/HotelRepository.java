package com.jmedina.hotel_template.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long>{

    List<Hotel> findByActiveTrue();

    //Buscar por ciudad
    List<Hotel> findByCityIgnoreCaseAndActiveTrue(String city);

    List<Hotel> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    boolean existsByEmail(String email);

    // Obtener hotel activo por id — para las páginas públicas
    Optional<Hotel> findByIdAndActiveTrue(Long id);
}
