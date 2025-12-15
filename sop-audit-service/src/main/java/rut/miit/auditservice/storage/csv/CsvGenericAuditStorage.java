package rut.miit.auditservice.storage.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rut.miit.auditservice.model.BaseModel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


abstract class CsvGenericAuditStorage<T extends BaseModel> implements GenericAuditStorage<T> {
    private static final Logger log = LoggerFactory.getLogger(CsvGenericAuditStorage.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}-\\d{2}-\\d{4})");

    private final Path storageDir;
    private final Class<T> entityClass;
    private final String filePrefix;
    private final long maxFileSizeBytes;
    private final ReadWriteLock lock;
    private final CsvMapper csvMapper;
    private final CsvSchema schema;

    protected CsvGenericAuditStorage(Class<T> entityClass, String baseDir, long maxFileSizeBytes) {
        this.entityClass = entityClass;
        this.filePrefix = getFilePrefixFromClass(entityClass);
        this.maxFileSizeBytes = maxFileSizeBytes * 1024 * 1024;
        this.lock = new ReentrantReadWriteLock();

        this.storageDir = Paths.get(baseDir, filePrefix);

        this.csvMapper = createCsvMapper();
        this.schema = csvMapper.schemaFor(entityClass).withHeader();

        initializeStorageDirectory();
    }

    private CsvMapper createCsvMapper() {
        CsvMapper mapper = new CsvMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
        return mapper;
    }

    private void initializeStorageDirectory() {
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                log.info("Created CSV storage directory: {}", storageDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create audit logs directory: {}", storageDir, e);
        }
    }

    private String getFilePrefixFromClass(Class<?> clazz) {
        String className = clazz.getSimpleName();
        return className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    @Override
    public void save(T record) {
        lock.writeLock().lock();
        try {
            Path currentFile = getCurrentFile();
            boolean fileExists = Files.exists(currentFile);

            if (fileExists && shouldRotate(currentFile)) {
                rotateFile(currentFile);
                fileExists = false;
            }

            CsvSchema writeSchema = fileExists ? schema.withoutHeader() : schema;


            try (FileWriter writer = new FileWriter(currentFile.toFile(), StandardCharsets.UTF_8, true)) {
                csvMapper.writerFor(entityClass)
                        .with(writeSchema)
                        .writeValue(writer, record);

                log.info("Saved {} record to {}", entityClass.getSimpleName(), currentFile.getFileName());
            }

        } catch (Exception e) {
            log.error("Failed to save {} record", entityClass.getSimpleName(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Path getCurrentFile() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        String fileName = String.format("%s_%s.csv", filePrefix, date);
        return storageDir.resolve(fileName);
    }

    private boolean shouldRotate(Path file) {
        try {
            long fileSize = Files.size(file);
            if (fileSize >= maxFileSizeBytes) {
                log.info("File {} needs rotation: size {} bytes exceeded max {} bytes", file.getFileName(), fileSize, maxFileSizeBytes);
                return true;
            }

            LocalDate fileDate = extractDateFromFileName(file);
            LocalDate today = LocalDate.now();

            if (fileDate != null && fileDate.isBefore(today)) {
                log.info("File {} needs rotation: date changed from {} to {}", file.getFileName(), fileDate, today);
                return true;
            }

            return false;

        } catch (IOException e) {
            log.warn("Failed to check if rotation needed for: {}", file, e);
            return false;
        }
    }

    private LocalDate extractDateFromFileName(Path file) {
        String fileName = file.getFileName().toString();
        Matcher matcher = DATE_PATTERN.matcher(fileName);

        if (matcher.find()) {
            try {
                String dateStr = matcher.group(1);
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                log.debug("Could not parse date from filename: {}", fileName, e);
            }
        }

        return null;
    }

    private void rotateFile(Path currentFile) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String currentFileName = currentFile.getFileName().toString();
            String rotatedName = currentFileName.replace(".csv", "_rotated_" + timestamp + ".csv");
            Path rotatedPath = storageDir.resolve(rotatedName);

            Files.move(currentFile, rotatedPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Rotated {} file to {}", entityClass.getSimpleName(), rotatedName);
        } catch (IOException e) {
            log.error("Failed to rotate file: {}", currentFile, e);
            throw new RuntimeException("Failed to rotate file", e);
        }
    }

    @Override
    public List<T> findAll(LocalDate from, LocalDate to) {
        lock.readLock().lock();
        try {
            List<T> allRecords = new ArrayList<>();
            List<Path> files = getFilesInDateRange(from, to);

            for (Path file : files) {
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    MappingIterator<T> it = csvMapper.readerFor(entityClass)
                            .with(schema)
                            .readValues(reader);

                    allRecords.addAll(it.readAll());
                }
            }

            log.info("Loaded {} {} records from {} files",
                    allRecords.size(), entityClass.getSimpleName(), files.size());
            return allRecords;

        } catch (Exception e) {
            log.error("Failed to load {} records", entityClass.getSimpleName(), e);
            return Collections.emptyList();
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<Path> getFilesInDateRange(LocalDate from, LocalDate to) {
        try (Stream<Path> paths = Files.list(storageDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv") && isInDateRange(path, from, to))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.error("Failed to list files in storage directory", e);
            return Collections.emptyList();
        }
    }

    private boolean isInDateRange(Path file, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return true;
        }

        LocalDate fileDate = extractDateFromFileName(file);
        if (fileDate == null) {
            return false;
        }

        boolean afterFrom = from == null || !fileDate.isBefore(from);
        boolean beforeTo = to == null || !fileDate.isAfter(to);

        return afterFrom && beforeTo;
    }

    @Override
    public boolean exists(UUID requestId) {
        if (requestId == null) {
            return false;
        }

        lock.readLock().lock();
        try {
            try (Stream<Path> paths = Files.list(storageDir)) {
                List<Path> csvFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".csv"))
                        .sorted().toList();

                for (Path file : csvFiles) {
                    if (fileContainsRequestId(file, requestId)) {
                        log.debug("Found requestId '{}' in file {}", requestId, file.getFileName());
                        return true;
                    }
                }
            }

            log.debug("RequestId '{}' not found in {} files", requestId, entityClass.getSimpleName());
            return false;

        } catch (IOException e) {
            log.error("Failed to check existence of requestId '{}'", requestId, e);
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean fileContainsRequestId(Path file, UUID requestId) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            MappingIterator<T> it = csvMapper.readerFor(entityClass).with(schema).readValues(reader);

            while (it.hasNext()) {
                T record = it.next();
                if (requestId.equals(record.getRequestId())) {
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            log.warn("Failed to read file {} during exists check", file.getFileName(), e);
            return false;
        }
    }


    @Override
    public void cleanupOlderThan(LocalDate cutoffDate) {
        lock.writeLock().lock();
        try {

            try (Stream<Path> paths = Files.list(storageDir)) {
                List<Path> oldFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".csv"))
                        .filter(path -> isOlderThan(path, cutoffDate))
                        .toList();

                int deletedCount = 0;
                for (Path file : oldFiles) {
                    try {
                        Files.delete(file);
                        log.info("Deleted old {} file: {}", entityClass.getSimpleName(), file.getFileName());
                        deletedCount++;
                    } catch (IOException e) {
                        log.error("Failed to delete file: {}", file, e);
                    }
                }

                log.info("Cleanup for {}: deleted {} of {} old files",
                        entityClass.getSimpleName(), deletedCount, oldFiles.size());
            }

        } catch (IOException e) {
            log.error("Failed to cleanup {} files", entityClass.getSimpleName(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isOlderThan(Path file, LocalDate cutoffDate) {
        LocalDate fileDate = extractDateFromFileName(file);

        if (fileDate == null) {
            String fileName = file.getFileName().toString();
            if (fileName.contains("_rotated_")) {
                String baseName = fileName.split("_rotated_")[0];
                Matcher matcher = DATE_PATTERN.matcher(baseName);
                if (matcher.find()) {
                    try {
                        fileDate = LocalDate.parse(matcher.group(1), DATE_FORMATTER);
                    } catch (DateTimeParseException e) {
                        log.debug("Could not parse date from rotated filename: {}", fileName);
                        return false;
                    }
                }
            }
        }

        return fileDate != null && fileDate.isBefore(cutoffDate);
    }

    @Override
    public void monitorStorage() {
        try {
            long totalSizeBytes = getStorageSize();
            long totalSizeMb = totalSizeBytes / 1024 / 1024;
            int fileCount = getFileCount();

            log.info("[{}] Storage stats - Size: {} MB, Files: {}", entityClass.getSimpleName(), totalSizeMb, fileCount);

            if (totalSizeMb > 1024) {
                log.warn("[{}] Storage size exceeded 1 GB! Current size: {} MB", entityClass.getSimpleName(), totalSizeMb);
            }

        } catch (Exception e) {
            log.error("[{}] Failed to monitor storage size", entityClass.getSimpleName(), e);
        }
    }

    @Override
    public int getFileCount() {
        try (Stream<Path> paths = Files.list(storageDir)) {
            return (int) paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv")).count();
        } catch (IOException e) {
            log.error("Failed to count files", e);
            return 0;
        }
    }

    @Override
    public long getStorageSize() {
        long totalSize = 0;

        try (Stream<Path> paths = Files.list(storageDir)) {
            for (Path path : paths.toList()) {
                if (!Files.isRegularFile(path) || !path.toString().endsWith(".csv")) {
                    continue;
                }
                try {
                    totalSize += Files.size(path);
                } catch (IOException e) {
                    log.warn("Failed to get size of file: {}", path, e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to calculate storage size", e);
        }

        return totalSize;
    }
}
