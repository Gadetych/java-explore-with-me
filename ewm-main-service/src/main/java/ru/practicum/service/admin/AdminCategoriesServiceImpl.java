package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;
import ru.practicum.mapper.AdminCategoriesMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.admin.AdminCategoriesRepository;
import ru.practicum.service.AdminCategoriesService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesServiceImpl implements AdminCategoriesService {
    private final AdminCategoriesRepository repository;

    @Override
    public CategoryDto create(NewCategoryDto requestBody) {
        log.debug("==> Create new category: {}", requestBody);
        Category model = repository.save(AdminCategoriesMapper.dtoToModel(requestBody));
        log.debug("<== Create new category: {}", model);
        return AdminCategoriesMapper.modelToDto(model);
    }

    @Override
    public void delete(long catId) {
        log.debug("==> Delete category: {}", catId);
        repository.deleteById((int) catId);
    }

    @Override
    public CategoryDto update(CategoryDto requestBody) {
        log.debug("==> Update category: {}", requestBody);
        Category model = repository.save(AdminCategoriesMapper.dtoToModel(requestBody));
        log.debug("<== Update category: {}", model);
        return AdminCategoriesMapper.modelToDto(model);
    }
}
