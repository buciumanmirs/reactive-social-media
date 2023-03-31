package com.mirothech.socialmediawebflux.comments;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CommentWriterRepository extends ReactiveCrudRepository<Comment,String> {
}
