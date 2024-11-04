package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;
import ru.practicum.model.Category;

@UtilityClass
public class AdminCategoriesMapper {
    public static Category dtoToModel(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public static Category dtoToModel(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static CategoryDto modelToDto(Category model) {
        return CategoryDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}
