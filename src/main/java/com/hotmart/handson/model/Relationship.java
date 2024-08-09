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
@Document(collection = "relationships")
@CompoundIndexes(
        @CompoundIndex(def = "{'user': 1, 'follower': 1}", unique = true)
)
public class Relationship {
    @Id
    private String uuid;
    private String user;
    private String follower;
    private Date createdAt;
}
