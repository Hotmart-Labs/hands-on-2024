package com.hotmart.handson.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
public class UserCreateVO {

    @NotEmpty
    @Length(min = 5, max = 100)
    private String name;

    @Pattern(regexp = "^(?=.{5,20}$)(?![_])(?!.*[_]{2})[a-zA-Z0-9_]+(?<![_])$")
    private String username;

    private String bio;

    private String location;

    @URL
    private String link;
}
