package com.mirothech.socialmediawebflux.images.repository;


import com.mirothech.socialmediawebflux.images.Image;
import com.mirothech.socialmediawebflux.images.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class EmbeddedImageRepositoryTests {

    @Autowired
    ImageRepository repository;

    @Autowired
    ReactiveMongoOperations operations;


    @BeforeEach
    void setup(){
        operations.insert(new Image("1",
                "learning-spring-boot-cover.jpg")).subscribe();
        operations.insert(new Image("2",
                "learning-spring-boot-2nd-edition-cover.jpg")).subscribe();
        operations.insert(new Image("3",
                "bazinga.png")).subscribe();
    }

    @Test
    void findAllShouldWork() {
        Flux<Image> images = repository.findAll();
        StepVerifier.create(images)
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith( results ->{
                    assertThat(results).hasSize(3);
                    assertThat(results)
                            .extracting(Image::getName)
                            .contains("bazinga.png");
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findByNameShouldWork() {
        Mono<Image> image = repository.findByName("bazinga.png");
        StepVerifier.create(image)
                .expectNextMatches(results -> {
                    assertThat(results.getName()).isEqualTo("bazinga.png");
                    assertThat(results.getId()).isEqualTo("3");
                    return true;
                });
    }
}