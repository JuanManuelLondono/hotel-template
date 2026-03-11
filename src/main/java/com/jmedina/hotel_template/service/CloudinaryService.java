package com.jmedina.hotel_template.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Sube una imagen y devuelve los datos de Cloudinary
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
    
        return (Map<String, Object>) cloudinary.uploader().upload(
        file.getBytes(),
        ObjectUtils.asMap(
            "folder", folder,
            "resource_type", "auto"
        )
        );
}

    // Elimina una imagen por su public_id
    public void deleteImage(String publicId) throws IOException{

        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
