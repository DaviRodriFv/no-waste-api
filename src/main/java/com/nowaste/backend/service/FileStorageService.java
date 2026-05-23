package com.nowaste.backend.service;

import com.nowaste.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${nowaste.upload.dir}")
    private String uploadDir;

    public String store(MultipartFile file, Long residuoId) {
        validatePdf(file);
        try {
            Path dir = Paths.get(uploadDir, "residuos", String.valueOf(residuoId)).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + sanitize(file.getOriginalFilename());
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return Paths.get("residuos", String.valueOf(residuoId), filename).toString();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo: " + e.getMessage(), e);
        }
    }

    public Path resolve(String relativePath) {
        return Paths.get(uploadDir).toAbsolutePath().normalize().resolve(relativePath).normalize();
    }

    public void delete(String relativePath) {
        try {
            Files.deleteIfExists(resolve(relativePath));
        } catch (IOException ignored) {
            // arquivo pode já ter sido removido
        }
    }

    private void validatePdf(MultipartFile file) {
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new BadRequestException("Apenas arquivos PDF são aceitos. Recebido: " + file.getContentType());
        }
    }

    private String sanitize(String filename) {
        if (filename == null) return "file.pdf";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
