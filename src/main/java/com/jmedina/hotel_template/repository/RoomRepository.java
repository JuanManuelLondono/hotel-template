package com.jmedina.hotel_template.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.Room;
import com.jmedina.hotel_template.model.Room.RoomStatus;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

    // Habitaciones de un hotel por estado
    List<Room> findByHotelIdAndStatus(Long hotelId, RoomStatus status);

    List<Room> findByRoomType_IdAndStatus(Long roomTypeId, RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
           "AND r.status = 'AVAILABLE' " +
           "AND r.active = true " +
           "AND r.id NOT IN (" +
               "SELECT res.room.id FROM Reservation res " +
               "WHERE res.status NOT IN ('CANCELLED', 'NO_SHOW') " +
               "AND res.checkIn < :checkOut " +     // se solapa si la reserva empieza antes de que el nuevo salga
               "AND res.checkOut > :checkIn" +      // y termina después de que el nuevo entra
           ")")
    List<Room> findAvailableRooms(
        @Param("hotelId") Long hotelId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut
    );

    // Retorna el número de reservas que se solapan
    // Si el resultado es 0, la habitación está disponible
    @Query("SELECT COUNT(res) FROM Reservation res " +
        "WHERE res.room.id = :roomId " +
        "AND res.status NOT IN ('CANCELLED', 'NO_SHOW') " +
        "AND res.checkIn < :checkOut " +
        "AND res.checkOut > :checkIn")
    Long countOverlappingReservations(
        @Param("roomId") Long roomId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut
    );
}
