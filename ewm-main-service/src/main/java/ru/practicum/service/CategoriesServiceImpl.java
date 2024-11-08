package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoriesRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository repository;

    //    Admin
    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto requestBody) {
        log.debug("==> Create new category: {}", requestBody);
        Category model = repository.save(CategoryMapper.dtoToModel(requestBody));
        log.debug("<== Create new category: {}", model);
        return CategoryMapper.modelToDto(model);
    }

    @Transactional
    @Override
    public void delete(long catId) {
        log.debug("==> Delete category: {}", catId);
        repository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto update(CategoryDto requestBody) {
        log.debug("==> Update category: {}", requestBody);
        Category model = repository.save(CategoryMapper.dtoToModel(requestBody));
        log.debug("<== Update category: {}", model);
        return CategoryMapper.modelToDto(model);
    }

    //    Public
    @Override
    public List<CategoryDto> findAll(int from, int size) {
        log.debug("==> Find all Categories from {}, size {}", from, size);
        List<Category> result = repository.findAllLimit(from, size);
        log.debug("<== Find all Categories from {}, size {}", from, size);
        return result.stream()
                .map(CategoryMapper::modelToDto)
                .toList();
    }

    @Override
    public CategoryDto findById(long catId) {
        log.debug("==> Find Category with id {}", catId);
        Category result = repository.findById(catId).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        log.debug("<== Find Category with id {}", catId);
        return CategoryMapper.modelToDto(result);
    }
}
