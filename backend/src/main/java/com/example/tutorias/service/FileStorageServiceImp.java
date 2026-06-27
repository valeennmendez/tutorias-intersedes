package com.example.tutorias.service;

import com.example.tutorias.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImp implements FileStorageService {

    // Se guardará en una carpeta 'upload/certificaciones' en la raíz del proyecto
    private final Path rootLocation = Paths.get("upload", "certificaciones");

    @Override
    public String guardarArchivo(MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                throw new ReglaNegocioException("El archivo PDF está vacío.");
            }
            
            // Validar que sea un PDF 
            String contentType = archivo.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                throw new ReglaNegocioException("El archivo debe ser un formato PDF válido.");
            }

            // Crear la carpeta si no existe
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Renombrar el archivo con un UUID para que no haya colisiones de nombres
            String nombreOriginal = archivo.getOriginalFilename();
            String extension = nombreOriginal != null ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : ".pdf";
            String nuevoNombre = UUID.randomUUID().toString() + extension;

            Path destino = this.rootLocation.resolve(nuevoNombre);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Retornamos la ruta relativa para guardar en la BD
            return destino.toString();

        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo almacenar el archivo PDF. Error: " + e.getMessage());
        }
    }
}
