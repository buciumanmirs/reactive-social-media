package com.mirothech.socialmediawebflux.comments;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentWriterRepository repository;

    private final MeterRegistry meterRegistry;

    public CommentService(CommentWriterRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue, exchange = @Exchange(value = "social-media-exchange"), key = "comments.new"))
    public void save(Comment newComment) {
        repository.save(newComment)
                .log("commentService-save")
                .subscribe(comment ->
                        meterRegistry.counter("comments.consumed", "imageId", comment.getImageId())
                                .increment()
                );
    }

}
