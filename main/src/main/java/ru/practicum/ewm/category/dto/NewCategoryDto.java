package ru.practicum.ewm.category.dto;

import lombok.*;
import ru.practicum.ewm.util.Create;
import ru.practicum.ewm.util.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(groups = {Create.class, Update.class}, message = "Name of the new category cannot be empty")
    @Size(groups = {Create.class, Update.class}, min = 1, max = 50,
            message = "Name of category cannot be less than 1 character and longer than 50 characters")
    private String name;
}
