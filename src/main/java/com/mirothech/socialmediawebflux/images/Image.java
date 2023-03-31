package com.mirothech.socialmediawebflux.images;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document
public class Image {

    @Id
    final private String id;
    final private String name;
}
