package com.jmedina.hotel_template.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long>{

    Optional<Amenity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
