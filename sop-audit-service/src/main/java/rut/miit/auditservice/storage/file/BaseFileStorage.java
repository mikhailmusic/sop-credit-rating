package rut.miit.auditservice.storage.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rut.miit.auditservice.model.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;


abstract class BaseFileStorage implements FileStorage {
    private static final Logger log = LoggerFactory.getLogger(BaseFileStorage.class);

    private final String baseDir;
    private final String allowedExtension;
    private final Path storageDir;

    public BaseFileStorage(String baseDir, String allowedExtension) {
        this.baseDir = baseDir;
        this.storageDir = Paths.get(baseDir);
        this.allowedExtension = allowedExtension;
        initializeDirectory();
    }

    private void initializeDirectory() {
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                log.info("Created storage directory: {}", storageDir.toAbsolutePath());
            } else {
                log.info("Storage directory exists: {}", storageDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create storage directory: {}", storageDir, e);
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }


    @Override
    public void save(String fileName, byte[] content) {
        validateFileName(fileName);

        try {
            Path filePath = storageDir.resolve(fileName);
            Files.write(filePath, content);
            log.info("File saved: {}", fileName);

        } catch (IOException e) {
            log.error("Failed to save file: {}", fileName, e);
            throw new RuntimeException("Failed to save file", e);
        }
    }


    public Optional<byte[]> findByFileName(String fileName) {
        validateFileName(fileName);

        Path filePath = storageDir.resolve(fileName);

        if (!Files.exists(filePath)) {
            log.debug("File not found: {}", fileName);
            return Optional.empty();
        }

        if (!Files.isReadable(filePath)) {
            log.warn("File not readable: {}", fileName);
            return Optional.empty();
        }

        try {
            return Optional.of(Files.readAllBytes(filePath));
        } catch (IOException e) {
            log.error("Failed to read file bytes: {}", fileName, e);
            return Optional.empty();
        }
    }

    @Override
    public List<FileInfo> findAllWithMetadata() {
        List<FileInfo> files = new ArrayList<>();

        try (Stream<Path> paths = Files.list(storageDir)) {
            List<Path> pathList = paths.toList();

            for (Path path : pathList) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String fileName = path.getFileName().toString();
                if (!fileName.endsWith(allowedExtension)) {
                    continue;
                }

                try {
                    long size = Files.size(path);

                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    LocalDateTime createdAt = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime modifiedAt = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                    files.add(new FileInfo(fileName, size, createdAt, modifiedAt));

                } catch (IOException e) {
                    log.warn("Failed to get info for file: {}", fileName, e);
                }
            }

        } catch (IOException e) {
            log.error("Failed to list files", e);
            return Collections.emptyList();
        }

        files.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        return files;
    }

    @Override
    public boolean delete(String fileName) {
        validateFileName(fileName);

        try {
            Path filePath = storageDir.resolve(fileName);

            if (!Files.exists(filePath)) {
                log.warn("File not found for deletion: {}", fileName);
                return false;
            }

            Files.delete(filePath);
            log.info("Deleted file: {}", fileName);
            return true;

        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
            return false;
        }
    }

    @Override
    public void cleanupOlderThan(LocalDate cutoffDate) {
        log.info("Starting cleanup of old files before: {}", cutoffDate);

        int deletedCount = 0;

        try {
            List<FileInfo> allFiles = findAllWithMetadata();

            List<FileInfo> filesToDelete = allFiles.stream()
                    .filter(fileInfo -> fileInfo.createdAt().isBefore(cutoffDate.atStartOfDay()))
                    .toList();

            for (FileInfo fileInfo : filesToDelete) {
                if (delete(fileInfo.fileName())) {
                    log.info("Deleted old file: {} (created: {})", fileInfo.fileName(), fileInfo.createdAt());
                    deletedCount++;
                } else {
                    log.warn("Failed to delete old file: {}", fileInfo.fileName());
                }
            }

            log.info("Cleanup completed. Deleted {} files out of {} candidates", deletedCount, filesToDelete.size());

        } catch (Exception e) {
            log.error("Failed during cleanup operation", e);
        }
    }

    @Override
    public boolean exists(String fileName) {
        validateFileName(fileName);
        Path filePath = storageDir.resolve(fileName);
        return Files.exists(filePath);
    }

    @Override
    public long getTotalSize() {
        long totalSize = 0;

        try (Stream<Path> paths = Files.list(storageDir)) {
            List<Path> pathList = paths.toList();

            for (Path path : pathList) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String fileName = path.getFileName().toString();
                if (!fileName.endsWith(allowedExtension)) {
                    continue;
                }

                try {
                    totalSize += Files.size(path);
                } catch (IOException e) {
                    log.warn("Failed to get size of file: {}", path, e);
                }
            }

        } catch (IOException e) {
            log.error("Failed to calculate total size", e);
        }

        return totalSize;
    }

    @Override
    public int getFileCount() {
        int count = 0;

        try (Stream<Path> paths = Files.list(storageDir)) {
            List<Path> pathList = paths.toList();

            for (Path path : pathList) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                String fileName = path.getFileName().toString();
                if (fileName.endsWith(allowedExtension)) {
                    count++;
                }
            }

        } catch (IOException e) {
            log.error("Failed to count files", e);
        }

        return count;
    }


    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        if (!fileName.endsWith(allowedExtension)) {
            throw new IllegalArgumentException(
                    "File must have " + allowedExtension + " extension"
            );
        }

        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
    }
}
