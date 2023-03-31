package com.mirothech.socialmediawebflux.comments;


import com.mirothech.socialmediawebflux.comments.Comment;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;

public interface CommentReaderRepository extends Repository<Comment, String> {
    Flux<Comment> findByImageId(String imageId);
}
