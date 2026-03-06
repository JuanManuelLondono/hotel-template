package com.jmedina.hotel_template.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Reservation;
import com.jmedina.hotel_template.model.Reservation.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>{

    // Reservas de un usuario — para "mis reservas" en el frontend
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Buscar por código — para que el cliente consulte su reserva
    Optional<Reservation> findByReservationCode(String reservationCode);

    // Reservas de un usuario por estado
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    // Reservas de un hotel — navegación profunda, mejor con @Query
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.room.hotel.id = :hotelId " +
           "ORDER BY r.checkIn ASC")
    List<Reservation> findByHotelId(@Param("hotelId") Long hotelId);

    // Reservas de un hotel por estado
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.room.hotel.id = :hotelId " +
           "AND r.status = :status")
    List<Reservation> findByHotelIdAndStatus(
        @Param("hotelId") Long hotelId,
        @Param("status") ReservationStatus status
    );

    // Check-ins del día — para el panel del hotel
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.room.hotel.id = :hotelId " +
           "AND r.checkIn = :checkIn " +
           "AND r.status = :status")
    List<Reservation> findTodayCheckIns(
        @Param("hotelId") Long hotelId,
        @Param("checkIn") LocalDate checkIn,
        @Param("status") ReservationStatus status
    );

    // Ingresos totales de un hotel
    @Query("SELECT SUM(r.finalPrice) FROM Reservation r " +
           "WHERE r.room.hotel.id = :hotelId " +
           "AND r.status IN ('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT')")
    BigDecimal getTotalRevenueByHotel(@Param("hotelId") Long hotelId);

    // Verificar si un usuario se hospedó en un hotel — para validar reseñas
    @Query("SELECT COUNT(r) FROM Reservation r " +
           "WHERE r.user.id = :userId " +
           "AND r.room.hotel.id = :hotelId " +
           "AND r.status = 'CHECKED_OUT'")
    Long countStaysByUserAtHotel(
        @Param("userId") Long userId,
        @Param("hotelId") Long hotelId
    ); 
}
