package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.BaseMapper;

import java.util.ArrayList;

@Component
public class ItemMapper implements BaseMapper<ItemDto, Item> {

    @Override
    public ItemDto toModelDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId()
        );
    }

    @Override
    public Item toModel(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwnerId());
    }

    public ItemDtoExt toModelDtoExt(Item item) {
        return new ItemDtoExt(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                item.getOwnerId());
    }
}
