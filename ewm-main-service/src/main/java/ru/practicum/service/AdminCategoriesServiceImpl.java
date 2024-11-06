package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoriesRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesServiceImpl implements AdminCategoriesService {
    //    TODO объединить с UserService
    private final CategoriesRepository repository;

    @Override
    public CategoryDto create(NewCategoryDto requestBody) {
        log.debug("==> Create new category: {}", requestBody);
        Category model = repository.save(CategoryMapper.dtoToModel(requestBody));
        log.debug("<== Create new category: {}", model);
        return CategoryMapper.modelToDto(model);
    }

    @Override
    public void delete(long catId) {
        log.debug("==> Delete category: {}", catId);
        repository.deleteById((int) catId);
    }

    @Override
    public CategoryDto update(CategoryDto requestBody) {
        log.debug("==> Update category: {}", requestBody);
        Category model = repository.save(CategoryMapper.dtoToModel(requestBody));
        log.debug("<== Update category: {}", model);
        return CategoryMapper.modelToDto(model);
    }
}
