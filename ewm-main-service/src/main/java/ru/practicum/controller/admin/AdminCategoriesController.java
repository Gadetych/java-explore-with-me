package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;
import ru.practicum.service.AdminCategoriesService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoriesController {
    private final AdminCategoriesService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid
                              @RequestBody NewCategoryDto requestBody) {
        log.info("==> Create new category: {}", requestBody);
        return service.create(requestBody);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive
                       @PathVariable long catId) {
        log.info("==> Delete category: {}", catId);
        service.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@Positive
                              @PathVariable long catId,
                              @RequestBody CategoryDto requestBody) {
        requestBody.setId(catId);
        log.info("==> Update category: {}", requestBody);
        return service.update(requestBody);
    }
}
