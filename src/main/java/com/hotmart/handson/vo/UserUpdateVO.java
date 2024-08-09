package com.hotmart.handson.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateVO {

    @NotEmpty
    @Length(min = 5, max = 100)
    private String name;

    private String bio;

    private String location;

    @URL
    private String link;
}
