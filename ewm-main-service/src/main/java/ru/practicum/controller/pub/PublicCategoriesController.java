package ru.practicum.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.service.PublicCategoriesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class PublicCategoriesController {
    private final PublicCategoriesService service;

    @GetMapping
    public List<CategoryDto> findAll(@PositiveOrZero
                                     @RequestParam(required = false, defaultValue = "0") int from,
                                     @PositiveOrZero
                                     @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("==> Find all categories from={}, size={}", from, size);
        return service.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto find(@Positive
                            @PathVariable long catId) {
        log.info("==> Find category by categoryId={}", catId);
        return service.find(catId);
    }
}
