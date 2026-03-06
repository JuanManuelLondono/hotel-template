package com.jmedina.hotel_template.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryResponseDTO {
    private Long id;
    private String imageUrl;
    private String altText;
    private String caption;
    private String category;
    private Integer displayOrder;
    private Integer width;
    private Integer height;
}