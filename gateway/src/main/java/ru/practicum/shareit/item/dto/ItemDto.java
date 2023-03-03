package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ItemDto {
    private long id;
    @NotBlank(message = "Наименование должно быть заполнено")
    private String name;
    @NotBlank(message = "Описание должно быть заполнено")
    private String description;
    @NotNull(message = "Лоступность должна быть заполнена")
    private Boolean available;
    private long ownerId;
    private Long requestId;
}
