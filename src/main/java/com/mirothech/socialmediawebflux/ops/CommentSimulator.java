package com.mirothech.socialmediawebflux.ops;

import com.mirothech.socialmediawebflux.comments.Comment;
import com.mirothech.socialmediawebflux.comments.CommentController;
import com.mirothech.socialmediawebflux.images.ImageRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Profile("simulator")
@Component
public class CommentSimulator {

    private final CommentController commentController;
    private final ImageRepository imageRepository;

    private final AtomicInteger counter;


    public CommentSimulator(CommentController commentController, ImageRepository imageRepository) {
        this.commentController = commentController;
        this.imageRepository = imageRepository;
        this.counter = new AtomicInteger(1);
    }


    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event){
        Flux.interval(Duration.ofMillis(10000))
                .flatMap(tick -> imageRepository.findAll())
                .map(image -> {
                    Comment comment = new Comment();
                    comment.setImageId(image.getId());
                    comment.setComment("Comment #" + counter.getAndIncrement());
                    return Mono.just(comment);
                })
                .flatMap(mewComment -> Mono.defer(()->
                        commentController.addComment(mewComment)))
                .subscribe();
    }
}
