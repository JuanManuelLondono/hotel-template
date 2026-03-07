package com.jmedina.hotel_template.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.RoomTypeRequestDTO;
import com.jmedina.hotel_template.dto.response.AmenityResponseDTO;
import com.jmedina.hotel_template.dto.response.RoomTypeResponseDTO;
import com.jmedina.hotel_template.dto.response.RoomTypeSummaryDTO;
import com.jmedina.hotel_template.model.Amenity;
import com.jmedina.hotel_template.model.Hotel;
import com.jmedina.hotel_template.model.Room.RoomStatus;
import com.jmedina.hotel_template.model.RoomType;
import com.jmedina.hotel_template.repository.AmenityRepository;
import com.jmedina.hotel_template.repository.HotelRepository;
import com.jmedina.hotel_template.repository.RoomRepository;
import com.jmedina.hotel_template.repository.RoomTypeRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final RoomRepository roomRepository;

    // Tipos activos de un hotel — para la página pública
    public List<RoomTypeResponseDTO> findByHotel(Long hotelId) {
        return roomTypeRepository.findByHotelIdAndActiveTrue(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener un tipo por ID
    public RoomTypeResponseDTO findById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de habitación no encontrado con id: " + id));
        return toResponseDTO(roomType);
    }

    // Crear tipo de habitación
    @Transactional
    public RoomTypeResponseDTO create(Long hotelId, RoomTypeRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel no encontrado con id: " + hotelId));

        RoomType roomType = RoomType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .pricePerNight(dto.getPricePerNight())
                .capacity(dto.getCapacity())
                .hotel(hotel)
                .build();

        // Asignar amenities si se enviaron
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
            roomType.setAmenities(amenities);
        }

        return toResponseDTO(roomTypeRepository.save(roomType));
    }

    // Actualizar tipo de habitación
    @Transactional
    public RoomTypeResponseDTO update(Long id, RoomTypeRequestDTO dto) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de habitación no encontrado con id: " + id));

        roomType.setName(dto.getName());
        roomType.setDescription(dto.getDescription());
        roomType.setPricePerNight(dto.getPricePerNight());
        roomType.setCapacity(dto.getCapacity());

        if (dto.getAmenityIds() != null) {
            List<Amenity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
            roomType.setAmenities(amenities);
        }

        return toResponseDTO(roomTypeRepository.save(roomType));
    }

    public RoomTypeResponseDTO toResponseDTO(RoomType rt) {
        // Contar habitaciones disponibles de este tipo
        int available = roomRepository
                .findByRoomType_IdAndStatus(rt.getId(), RoomStatus.AVAILABLE)
                .size();

        List<AmenityResponseDTO> amenities = rt.getAmenities() == null ? List.of() :
                rt.getAmenities().stream()
                        .map(a -> AmenityResponseDTO.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .icon(a.getIcon())
                                .build())
                        .collect(Collectors.toList());

        return RoomTypeResponseDTO.builder()
                .id(rt.getId())
                .name(rt.getName())
                .description(rt.getDescription())
                .pricePerNight(rt.getPricePerNight())
                .capacity(rt.getCapacity())
                .coverImageUrl(rt.getCoverImageUrl())
                .active(rt.isActive())
                .hotelId(rt.getHotel().getId())
                .hotelName(rt.getHotel().getName())
                .amenities(amenities)
                .availableRooms(available)
                .build();
    }

    public RoomTypeSummaryDTO toSummaryDTO(RoomType rt) {
        int available = roomRepository
                .findByRoomType_IdAndStatus(rt.getId(), RoomStatus.AVAILABLE)
                .size();
        return RoomTypeSummaryDTO.builder()
                .id(rt.getId())
                .name(rt.getName())
                .pricePerNight(rt.getPricePerNight())
                .capacity(rt.getCapacity())
                .coverImageUrl(rt.getCoverImageUrl())
                .availableRooms(available)
                .build();
    }
}
