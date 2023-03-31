package com.mirothech.socialmediawebflux.images;

import com.mirothech.socialmediawebflux.images.Image;
import com.mirothech.socialmediawebflux.images.ImageRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    private static final String UPLOAD_ROOT = "upload-dir";

    private final ResourceLoader resourceLoader;

    private final ImageRepository imageRepository;

    private final MeterRegistry meterRegistry;

    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository, MeterRegistry meterRegistry) {
        this.resourceLoader = resourceLoader;
        this.imageRepository = imageRepository;
        this.meterRegistry = meterRegistry;
    }

    public Flux<Image> findAllImagesWithResourceLoader() {
        try {
            return Flux.fromIterable(
                            Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
                    .map(path -> new Image(String.valueOf(path.hashCode()), path.getFileName().toString()));

        } catch (IOException e) {
            return Flux.error(e);
        }
    }

    public Flux<Image> findAllImages() {
        return imageRepository.findAll()
                .log("findAll");
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(file -> {
            Mono<Image> saveDatabaseImage = imageRepository.save(new Image(
                    UUID.randomUUID().toString(), file.filename()));
            Mono<Void> copyFile = Mono.just(
                            Paths.get(UPLOAD_ROOT, file.filename())
                                    .toFile())
                    .log("createImage-picktarget")
                    .map(destFile -> {
                        try {
                            destFile.createNewFile();
                            return destFile;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }).log("createImage-newfile")
                    .flatMap(file::transferTo)
                    .log("createImage-copu");

            Mono<Void> countFile = Mono.fromRunnable(() -> {
                meterRegistry
                        .summary("files.uploaded.bytes")
                        .record(Paths.get(UPLOAD_ROOT, file.filename()).toFile().length());
            });

            return Mono.when(saveDatabaseImage, copyFile, countFile);
        }).then();

    }

    public Mono<Resource> findOneImage(String fileName) {
        return Mono.fromSupplier(() ->
                resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + fileName));
    }

    public Mono<Void> deleteImage(String fileName) {
        Mono<Void> deleteDatabaseImage = imageRepository
                .findByName(fileName)
                .log("deleteImage-find")
                .flatMap(imageRepository::delete)
                .log("deleteImage-record");

        Mono<Void> deleteFile = Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });

        return Mono.when(deleteDatabaseImage, deleteFile)
                .log("deleteImage-when")
                .then()
                .log("deleteImage-done");
    }

    @Bean
    CommandLineRunner setup() {

        return args -> {
            FileSystemUtils.deleteRecursively(Paths.get(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
//            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/learning-spring-boot-cover.jpg"));
//            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/learning-spring-2nd-edition-cover.jpg"));
//            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/bazinga.png"));

        };
    }
}
