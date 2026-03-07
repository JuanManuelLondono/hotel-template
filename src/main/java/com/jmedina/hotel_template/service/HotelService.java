package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.HotelRequestDTO;
import com.jmedina.hotel_template.dto.response.HotelResponseDTO;
import com.jmedina.hotel_template.dto.response.HotelSummaryDTO;
import com.jmedina.hotel_template.model.Hotel;
import com.jmedina.hotel_template.repository.HotelRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    // Listar hoteles activos — para el frontend público
    public List<HotelSummaryDTO> findAllActive() {
        return hotelRepository.findByActiveTrue()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Obtener hotel por ID con toda su info
    public HotelResponseDTO findById(Long id) {
        Hotel hotel = hotelRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado con id: " + id));
        return toResponseDTO(hotel);
    }

    // Buscar por ciudad
    public List<HotelSummaryDTO> findByCity(String city) {
        return hotelRepository.findByCityIgnoreCaseAndActiveTrue(city)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Hoteles mejor calificados
    public List<HotelSummaryDTO> findTopRated() {
        return hotelRepository.findByActiveTrueOrderByAverageRatingDesc()
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Crear hotel — solo ADMIN
    @Transactional
    public HotelResponseDTO create(HotelRequestDTO dto) {
        if (dto.getEmail() != null && hotelRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un hotel con ese email");
        }

        Hotel hotel = Hotel.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .website(dto.getWebsite())
                .checkInTime(dto.getCheckInTime())
                .checkOutTime(dto.getCheckOutTime())
                .cancellationPolicy(dto.getCancellationPolicy())
                .build();

        return toResponseDTO(hotelRepository.save(hotel));
    }

    // Actualizar hotel
    @Transactional
    public HotelResponseDTO update(Long id, HotelRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado con id: " + id));

        hotel.setName(dto.getName());
        hotel.setDescription(dto.getDescription());
        hotel.setAddress(dto.getAddress());
        hotel.setCity(dto.getCity());
        hotel.setCountry(dto.getCountry());
        hotel.setLatitude(dto.getLatitude());
        hotel.setLongitude(dto.getLongitude());
        hotel.setPhone(dto.getPhone());
        hotel.setEmail(dto.getEmail());
        hotel.setWebsite(dto.getWebsite());
        hotel.setCheckInTime(dto.getCheckInTime());
        hotel.setCheckOutTime(dto.getCheckOutTime());
        hotel.setCancellationPolicy(dto.getCancellationPolicy());

        return toResponseDTO(hotelRepository.save(hotel));
    }

    // Actualizar calificación promedio — llamado desde ReviewService
    @Transactional
    public void updateAverageRating(Long hotelId, Double newAverage, Long totalReviews) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado"));
        hotel.setAverageRating(newAverage != null ? newAverage : 0.0);
        hotel.setTotalReviews(totalReviews.intValue());
        hotelRepository.save(hotel);
    }

    // Desactivar hotel — soft delete
    @Transactional
    public void deactivate(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado con id: " + id));
        hotel.setActive(false);
        hotelRepository.save(hotel);
    }

    public HotelResponseDTO toResponseDTO(Hotel hotel) {
        return HotelResponseDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                .phone(hotel.getPhone())
                .email(hotel.getEmail())
                .website(hotel.getWebsite())
                .coverImageUrl(hotel.getCoverImageUrl())
                .averageRating(hotel.getAverageRating())
                .totalReviews(hotel.getTotalReviews())
                .checkInTime(hotel.getCheckInTime())
                .checkOutTime(hotel.getCheckOutTime())
                .cancellationPolicy(hotel.getCancellationPolicy())
                .active(hotel.isActive())
                .createdAt(hotel.getCreatedAt())
                .build();
    }

    public HotelSummaryDTO toSummaryDTO(Hotel hotel) {
        return HotelSummaryDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .country(hotel.getCountry())
                .coverImageUrl(hotel.getCoverImageUrl())
                .averageRating(hotel.getAverageRating())
                .totalReviews(hotel.getTotalReviews())
                .build();
    }
}
