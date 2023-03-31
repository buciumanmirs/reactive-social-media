package com.mirothech.socialmediawebflux.comments;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

public interface CommentWriterRepository extends Repository<Comment, String> {

    Mono<Comment> save(Comment comment);

    Mono<Comment> findById(String id);

}
