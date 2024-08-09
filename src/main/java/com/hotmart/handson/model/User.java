package com.hotmart.handson.model;

import com.hotmart.handson.mongodb.mapping.CollectionOptions;
import com.hotmart.handson.mongodb.mapping.Index;
import com.hotmart.handson.mongodb.mapping.Indexes;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@CollectionOptions(capped = true)
@Indexes(
        @Index(field = "username", unique = true)
)
public class User {
    @Id
    private String uuid;
    @NotEmpty
    @Length(min = 5, max = 100)
    private String name;
    @Indexed(unique = true)
    @NotEmpty
    @Pattern(regexp = "^(?=.{5,20}$)(?![_])(?!.*[_]{2})[a-zA-Z0-9_]+(?<![_])$")
    private String username;
    @NotNull
    private Date joinedAt;
    @URL
    private String picture;
    @URL
    private String banner;
    private String bio;
    private String location;
    private String link;
    private boolean verified;
    @Builder.Default
    private UserStats stats = UserStats.builder().build();
    @Transient
    private boolean following;
}
