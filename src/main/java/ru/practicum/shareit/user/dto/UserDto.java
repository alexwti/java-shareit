package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private long id;

    @NotBlank(message = "Имя должно быть заполнено")
    private String name;

    @NotNull(message = "EMail должен быть заполнен")
    @Email(message = "EMail не соответствует шаблону")
    private String email;
}
