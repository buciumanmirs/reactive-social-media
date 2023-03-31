package com.mirothech.socialmediawebflux.comments;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentWriterRepository repository;

    public CommentService(CommentWriterRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue, exchange = @Exchange(value = "social-media-exchange"), key = "comments.new"))
    public void save(Comment newComment) {
        repository.save(newComment)
                .log("commentService-save")
                .subscribe();
    }

}
