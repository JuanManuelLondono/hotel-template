package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.AmenityRequestDTO;
import com.jmedina.hotel_template.dto.response.AmenityResponseDTO;
import com.jmedina.hotel_template.model.Amenity;
import com.jmedina.hotel_template.repository.AmenityRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public List<AmenityResponseDTO> findAll() {
        return amenityRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AmenityResponseDTO create(AmenityRequestDTO dto) {
        if (amenityRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Ya existe un amenity con ese nombre");
        }
        Amenity amenity = Amenity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .build();
        return toResponseDTO(amenityRepository.save(amenity));
    }

    @Transactional
    public AmenityResponseDTO update(Long id, AmenityRequestDTO dto) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Amenity no encontrado con id: " + id));
        amenity.setName(dto.getName());
        amenity.setIcon(dto.getIcon());
        return toResponseDTO(amenityRepository.save(amenity));
    }

    @Transactional
    public void delete(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new EntityNotFoundException("Amenity no encontrado con id: " + id);
        }
        amenityRepository.deleteById(id);
    }

    public AmenityResponseDTO toResponseDTO(Amenity a) {
        return AmenityResponseDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .icon(a.getIcon())
                .build();
    }
}
