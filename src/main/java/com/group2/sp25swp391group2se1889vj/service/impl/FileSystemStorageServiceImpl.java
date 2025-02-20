package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.config.StorageProperties;
import com.group2.sp25swp391group2se1889vj.exception.StorageException;
import com.group2.sp25swp391group2se1889vj.exception.StorageFileNotFoundException;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageServiceImpl implements StorageService {
    private final Path tempLocation;
    private final Path rootLocation;

    @Autowired
    public FileSystemStorageServiceImpl(StorageProperties properties) {

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
        this.tempLocation = Paths.get(properties.getTempLocation());

    }




    @Override
    public String store(MultipartFile file) {
        String extension = Objects.requireNonNull(FilenameUtils
                .getExtension(file.getOriginalFilename())).toLowerCase();
        String filename = UUID.randomUUID() + "." + extension;
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return String.join("/", "upload/files", filename);
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public String saveToTemp(MultipartFile file) {
        String extension = Objects.requireNonNull(FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();
        String filename = UUID.randomUUID() + "." + extension;

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.tempLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.tempLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside temp directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return String.join("/", "upload/temps", filename);
        } catch (IOException e) {
            throw new StorageException("Failed to store file in temp.", e);
        }
    }

    public String moveToUploads(String filename) {
        filename = filename.substring(filename.lastIndexOf("/") + 1);
        // check file is exist in uploads
        if (Files.exists(rootLocation.resolve(filename))) {
            return String.join("/", "upload/files", filename);
        }
        try {
            Path sourceFile = tempLocation.resolve(filename).normalize().toAbsolutePath();
            Path destinationFile = rootLocation.resolve(filename).normalize().toAbsolutePath();

            if (!Files.exists(sourceFile)) {
                throw new StorageException("File not found in temp: " + filename);
            }

            Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return String.join("/", "upload/files", filename);
        } catch (IOException e) {
            throw new StorageException("Failed to move file to uploads.", e);
        }
    }

    /**
     * Loads all paths. The returned Stream must be closed by the caller.
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path loadTemp(String filename) {
        return tempLocation.resolve(filename);
    }

    @Override
    public Resource loadTempAsResource(String filename) {
        try {
            Path file = loadTemp(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }


    @Override
    public boolean isValidSize(MultipartFile file, int maxSize) {
        return file.getSize() <= (long) maxSize * 1024 * 1024;
    }

    @Override
    public boolean hasValidExtension(@NotNull MultipartFile file, String[] allowedExtensions) {
        var filename = file.getOriginalFilename();
        var fileExtension = FilenameUtils.getExtension(filename);
        return Arrays.asList(allowedExtensions).contains(fileExtension);
    }

    @Override
    public boolean isImage(MultipartFile file) {
        if (!hasValidExtension(file, new String[]{ "jpg", "jpeg", "png", "gif", "bmp", "webp" })) {
            return false;
        }
        Tika tika = new Tika();
        return tika.detect(file.getOriginalFilename()).startsWith("image");
    }

    @Override
    public void deleteFile(String filename) {
        // get file name from path
        filename = filename.substring(filename.lastIndexOf("/") + 1);
        try {
            Files.deleteIfExists(rootLocation.resolve(filename));
        } catch (IOException e) {
            throw new StorageException("Failed to delete file.", e);
        }
    }

    @Override
    public void init() {
        if (Files.exists(rootLocation)) {
            return;
        }
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }

        if (Files.exists(tempLocation)) {
            return;
        }
        try {
            Files.createDirectories(tempLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize temp storage", e);
        }
    }
}
