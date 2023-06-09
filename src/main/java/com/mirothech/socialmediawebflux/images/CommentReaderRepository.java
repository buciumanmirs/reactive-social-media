package com.mirothech.socialmediawebflux.images;


import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;

public interface CommentReaderRepository extends Repository<Comment, String> {
    Flux<Comment> findByImageId(String imageId);
}
