package com.example.tutorias.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String guardarArchivo(MultipartFile archivo);
}