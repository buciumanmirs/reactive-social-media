package com.mirothech.socialmediawebflux.comments;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
public class CommentController {

    private final RabbitTemplate rabbitTemplate;

    private final MeterRegistry meterRegistry;


    public CommentController(RabbitTemplate rabbitTemplate, MeterRegistry meterRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/comments")
    public Mono<String> addComment(Mono<Comment> newComment){
        return newComment.flatMap(comment ->
                Mono.fromRunnable(()->
                        rabbitTemplate
                                .convertAndSend("social-media-exchange",
                                        "comments.new",
                                        comment))
                .then(Mono.just(comment)))
                .log("commentService-publish")
                .flatMap(comment ->  {
                    meterRegistry.counter("coments.produced", "imageId", comment.getImageId())
                            .increment();

                    return Mono.just("redirect:/");
                });
    }
}
