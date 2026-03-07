package com.jmedina.hotel_template.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jmedina.hotel_template.dto.request.ReservationRequestDTO;
import com.jmedina.hotel_template.dto.response.ReservationResponseDTO;
import com.jmedina.hotel_template.model.Reservation;
import com.jmedina.hotel_template.model.Reservation.ReservationStatus;
import com.jmedina.hotel_template.model.Room;
import com.jmedina.hotel_template.model.User;
import com.jmedina.hotel_template.repository.ReservationRepository;
import com.jmedina.hotel_template.repository.RoomRepository;
import com.jmedina.hotel_template.repository.RoomTypeRepository;
import com.jmedina.hotel_template.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;

    // Reservas del usuario autenticado
    public List<ReservationResponseDTO> findByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Reservas de un hotel — para el panel admin
    public List<ReservationResponseDTO> findByHotel(Long hotelId) {
        return reservationRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar por código — para consulta del cliente
    public ReservationResponseDTO findByCode(String code) {
        Reservation reservation = reservationRepository.findByReservationCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con código: " + code));
        return toResponseDTO(reservation);
    }

    // Crear reserva — la lógica más importante del sistema
    @Transactional
    public ReservationResponseDTO create(Long userId, ReservationRequestDTO dto) {

        // 1. Validar que checkOut sea después de checkIn
        if (!dto.getCheckOut().isAfter(dto.getCheckIn())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada");
        }

        // 2. Validar que checkIn no sea en el pasado
        if (dto.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser en el pasado");
        }

        // 3. Buscar habitaciones disponibles del tipo solicitado
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                getRoomTypeHotelId(dto.getRoomTypeId()),
                dto.getCheckIn(),
                dto.getCheckOut()
        ).stream()
                .filter(r -> r.getRoomType().getId().equals(dto.getRoomTypeId()))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) {
            throw new IllegalStateException("No hay habitaciones disponibles para esas fechas");
        }

        // 4. Tomar la primera habitación disponible
        Room room = availableRooms.get(0);

        // 5. Validar capacidad
        if (dto.getGuestsCount() > room.getRoomType().getCapacity()) {
            throw new IllegalArgumentException(
                "La habitación tiene capacidad máxima de " + room.getRoomType().getCapacity() + " personas"
            );
        }

        // 6. Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // 7. Crear la reserva — los cálculos de precio los hace @PrePersist en el modelo
        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .guestsCount(dto.getGuestsCount())
                .pricePerNight(room.getRoomType().getPricePerNight())
                .specialRequests(dto.getSpecialRequests())
                .build();

        return toResponseDTO(reservationRepository.save(reservation));
    }

    // Cancelar reserva
    @Transactional
    public ReservationResponseDTO cancel(Long reservationId, Long userId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        // Verificar que la reserva pertenece al usuario
        if (!reservation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta reserva");
        }

        // Solo se pueden cancelar reservas PENDING o CONFIRMED
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN ||
            reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("No se puede cancelar una reserva en curso o finalizada");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        reservation.setCancelledAt(java.time.LocalDateTime.now());

        return toResponseDTO(reservationRepository.save(reservation));
    }

    // Confirmar pago — llamado cuando el pago es exitoso
    @Transactional
    public ReservationResponseDTO confirmPayment(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPaidAt(java.time.LocalDateTime.now());

        return toResponseDTO(reservationRepository.save(reservation));
    }

    // Método auxiliar para obtener el hotelId de un roomType
    private Long getRoomTypeHotelId(Long roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de habitación no encontrado"))
                .getHotel().getId();
    }

    public ReservationResponseDTO toResponseDTO(Reservation r) {
        return ReservationResponseDTO.builder()
                .id(r.getId())
                .reservationCode(r.getReservationCode())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .userEmail(r.getUser().getEmail())
                .roomId(r.getRoom().getId())
                .roomNumber(r.getRoom().getRoomNumber())
                .roomTypeName(r.getRoom().getRoomType().getName())
                .hotelName(r.getRoom().getHotel().getName())
                .checkIn(r.getCheckIn())
                .checkOut(r.getCheckOut())
                .totalNights(r.getTotalNghts())
                .pricePerNight(r.getPricePerNight())
                .totalPrice(r.getTotalPrice())
                .taxAmount(r.getTaxAmount())
                .finalPrice(r.getFinalPrice())
                .guestsCount(r.getGuestsCount())
                .specialRequests(r.getSpecialRequests())
                .status(r.getStatus().name())
                .createdAt(r.getCreatedAt())
                .paidAt(r.getPaidAt())
                .build();
    }
}
