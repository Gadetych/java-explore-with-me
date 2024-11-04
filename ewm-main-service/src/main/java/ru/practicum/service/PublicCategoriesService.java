package ru.practicum.service;

import ru.practicum.dto.admin.CategoryDto;

import java.util.List;

public interface PublicCategoriesService {
    List<CategoryDto> findAll(int from, int size);

    CategoryDto find(long catId);
}
