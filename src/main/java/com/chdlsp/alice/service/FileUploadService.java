package com.chdlsp.alice.service;

import com.chdlsp.alice.config.FileUploadConfig;
import com.chdlsp.alice.domain.entity.ImageUploadEntity;
import com.chdlsp.alice.domain.repository.ImageUploadRepository;
import com.chdlsp.alice.interfaces.exception.StorageException;
import com.chdlsp.alice.interfaces.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class FileUploadService {

    private final Path rootLocation;

    @Autowired
    private ImageUploadRepository imageUploadRepository;

    @Autowired
    public FileUploadService(FileUploadConfig properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    public void store(MultipartFile file, String email) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        // 파일저장 처리
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // 이미지 업로드 정보 저장 처리
            ImageUploadEntity imageUploadEntity = ImageUploadEntity.builder()
                    .imageName(filename)
                    .imageSize(file.getSize())
                    .uploadUser(email)
                    .createdAt(LocalDateTime.now())
                    .build();

            imageUploadRepository.save(imageUploadEntity);

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public UrlResource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            UrlResource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
