package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoriesService {
    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(long catId);
}
