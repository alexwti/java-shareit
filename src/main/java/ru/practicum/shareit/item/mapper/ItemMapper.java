package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExt;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.BaseMapper;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class ItemMapper implements BaseMapper<ItemDto, Item> {

    @Override
    public ItemDto toModelDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null)
        );
    }

    @Override
    public Item toModel(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwnerId(),
                itemDto.getRequestId() == null ? null : ItemRequest.builder().id(itemDto.getRequestId()).build()
        );
    }

    public ItemDtoExt toModelDtoExt(Item item) {
        return new ItemDtoExt(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>(),
                item.getOwnerId(),
                Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null)
        );
    }
}
