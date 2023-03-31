package com.mirothech.socialmediawebflux.comments;

import com.mirothech.socialmediawebflux.comments.Comment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
public class CommentController {

    private final RabbitTemplate rabbitTemplate;


    public CommentController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/comments")
    public Mono<String> addComment(Mono<Comment> newComment){
        return newComment.flatMap(comment ->
                Mono.fromRunnable(()->
                        rabbitTemplate.convertAndSend("social-media-exchange", "comments.new", comment)))
                .log("commentService-publish")
                .then(Mono.just("redirect:/"));
    }
}
