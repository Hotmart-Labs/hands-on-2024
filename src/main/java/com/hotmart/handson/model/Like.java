package com.hotmart.handson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
@CompoundIndexes(
        @CompoundIndex(def = "{'tweet': 1, 'user': 1}", unique = true)
)
public class Like {
    @Id
    private String uuid;
    private String tweet;
    private String user;
    private Date createdAt;
}
