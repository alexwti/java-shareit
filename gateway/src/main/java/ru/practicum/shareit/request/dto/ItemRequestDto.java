package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(groups = Create.class, min = 1, max = 200)
    private String description;
    private Long requesterid;
    private LocalDateTime created;
    private List<ItemDto> items;
}
