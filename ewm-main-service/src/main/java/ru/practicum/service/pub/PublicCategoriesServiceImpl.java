package ru.practicum.service.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.AdminCategoriesMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.admin.AdminCategoriesRepository;
import ru.practicum.service.PublicCategoriesService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PublicCategoriesServiceImpl implements PublicCategoriesService {
    private final AdminCategoriesRepository repository;

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        log.debug("==> Find all Categories from {}, size {}", from, size);
        List<Category> result = repository.findAllLimit(from, size);
        log.debug("<== Find all Categories from {}, size {}", from, size);
        return result.stream()
                .map(AdminCategoriesMapper::modelToDto)
                .toList();
    }

    @Override
    public CategoryDto find(long catId) {
        log.debug("==> Find Category with id {}", catId);
        Category result = repository.findById((int) catId).orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        log.debug("<== Find Category with id {}", catId);
        return AdminCategoriesMapper.modelToDto(result);
    }
}
