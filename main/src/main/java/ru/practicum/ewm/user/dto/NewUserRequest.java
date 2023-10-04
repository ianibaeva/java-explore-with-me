package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 250, message = "Name cannot be less than 2 characters and more than 250 characters")
    private String name;
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email address must not be blank")
    @Size(min = 6, max = 254, message = "Email address cannot be less than 6 characters and more than 254 characters")
    private String email;
}
