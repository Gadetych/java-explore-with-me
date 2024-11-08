package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoriesRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
class CategoriesServiceImplTest {
    @Autowired
    private CategoriesServiceImpl service;
    @Autowired
    private CategoriesRepository repository;

    //    Admin
    long id;
    String name = "name";
    NewCategoryDto newCategoryDto = NewCategoryDto.builder()
            .name(name)
            .build();
    //Public
    long id1;
    long id2;
    String name1 = "name1";
    String name2 = "name2";
    Category model1 = Category.builder()
            .name(name1)
            .build();
    Category model2 = Category.builder()
            .name(name2)
            .build();


    @BeforeEach
    void setUp() {
        id = service.create(newCategoryDto).getId();
        id1 = repository.save(model1).getId();
        id2 = repository.save(model2).getId();
    }

    //Admin
    @Test
    void create() {
        String name3 = "name3";
        NewCategoryDto requestDto = NewCategoryDto.builder()
                .name(name3)
                .build();
        CategoryDto result = service.create(requestDto);

        assertNotNull(result);
        assertTrue(result.getId() > 0);
        assertEquals(name3, result.getName());
    }

    @Test
    void delete() {
        service.delete(id);

        assertTrue(repository.findById(id).isEmpty());
    }

    @Test
    void update() {
        String newName = "newName";
        CategoryDto dto = CategoryDto.builder()
                .id(id)
                .name(newName)
                .build();
        service.update(dto);

        Category result = repository.findById(id).get();

        assertNotNull(result);
        assertEquals(newName, result.getName());
    }

    //    Public
    @Test
    void findAll_shouldReturnListSize2() {
        int from = 0;
        int size = 2;
        List<CategoryDto> result = service.findAll(from, size);
        assertFalse(result.isEmpty());
        assertEquals(size, result.size());
        assertTrue(result.get(0).getId() < result.get(1).getId());
    }

    @Test
    void findAll_shouldReturnListSize1() {
        int from = 1;
        int size = 1;
        List<CategoryDto> result = service.findAll(from, size);
        assertFalse(result.isEmpty());
        assertEquals(size, result.size());
    }

    @Test
    void find_ById_shouldReturnNotNull() {
        CategoryDto result = service.findById(id1);

        assertNotNull(result);
        assertEquals(id1, result.getId());
        assertEquals(name1, result.getName());
    }
}