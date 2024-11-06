package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

public interface AdminCategoriesService {
    CategoryDto create(NewCategoryDto requestBody);

    void delete(long catId);

    CategoryDto update(CategoryDto requestBody);
}
