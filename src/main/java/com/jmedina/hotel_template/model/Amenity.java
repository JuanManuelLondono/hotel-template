package com.jmedina.hotel_template.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del amenity es obligatorio")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String  icon;

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    private List<RoomType> roomTypes;
}
