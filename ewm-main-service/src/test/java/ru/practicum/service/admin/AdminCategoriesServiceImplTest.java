package ru.practicum.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.dto.admin.NewCategoryDto;
import ru.practicum.model.Category;
import ru.practicum.repository.admin.AdminCategoriesRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
class AdminCategoriesServiceImplTest {
    @Autowired
    private AdminCategoriesServiceImpl service;
    @Autowired
    private AdminCategoriesRepository repository;

    long id;
    String name = "name";
    NewCategoryDto newCategoryDto = NewCategoryDto.builder()
            .name(name)
            .build();

    @BeforeEach
    void setUp() {
        id = service.create(newCategoryDto).getId();
    }

    @Test
    void create() {
        String name1 = "name1";
        NewCategoryDto requestDto = NewCategoryDto.builder()
                .name(name1)
                .build();
        CategoryDto result = service.create(requestDto);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(name1, result.getName());
    }

    @Test
    void delete() {
        service.delete(id);

        assertTrue(repository.findById((int) id).isEmpty());
    }

    @Test
    void update() {
        String newName = "newName";
        CategoryDto dto = CategoryDto.builder()
                .id(id)
                .name(newName)
                .build();
        service.update(dto);

        Category result = repository.findById((int) id).get();

        assertNotNull(result);
        assertEquals(newName, result.getName());
    }
}