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
    @Size // установить минималку по символам
    private String name;
    @Email(message = "Invalid email address")
    @NotBlank
    @Size // установить минималку по символам
    private String email;
}
