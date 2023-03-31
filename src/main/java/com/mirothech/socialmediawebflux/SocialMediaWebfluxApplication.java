package com.mirothech.socialmediawebflux;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

@SpringBootApplication
public class SocialMediaWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaWebfluxApplication.class, args);
    }

    /*
     We must add a HiddenHttpMethodFilter spring bean to make the HTTP DELETE methods work properly
         DELETE is not a valid action for an HTML5 FORM, so Thymeleaf creates a hidden input field
        containing our desired verb while the enclosing form uses an HTML5 POST. This gets
        transformed by Spring during the web call, resulting in the @DeleteMapping method being
        properly invoked with no ef ort on our end
     */
    @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
