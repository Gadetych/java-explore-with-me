package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoriesService {
    //    Admin
    CategoryDto create(NewCategoryDto requestBody);

    void delete(long catId);

    CategoryDto update(CategoryDto requestBody);

    //    Public
    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(long catId);
}
