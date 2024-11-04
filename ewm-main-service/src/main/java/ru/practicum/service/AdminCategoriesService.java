package ru.practicum.service;

import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;

public interface AdminCategoriesService {
    CategoryDto create(NewCategoryDto requestBody);

    void delete(long catId);

    CategoryDto update(CategoryDto requestBody);
}
