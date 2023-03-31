package com.mirothech.socialmediawebflux.ops;

import com.mirothech.socialmediawebflux.images.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitDatabase {


    @Bean
    CommandLineRunner init(ReactiveMongoOperations operations) {
        return args -> {
            operations.dropCollection(Image.class).subscribe();
            operations.insert(new Image("1", "learning-spring-cover.jpg")).subscribe();
            operations.insert(new Image("2", "learning-spring-2nd-edition-cover.jpg")).subscribe();
            operations.insert(new Image("3", "bazinga.jpg")).subscribe();

            operations.findAll(Image.class).log().subscribe();
        };
    }
}
