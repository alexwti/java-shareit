package ru.practicum.shareit.util;

public interface BaseMapper<D, M> {

    D toModelDto(M m);

    M toModel(D d);

}
