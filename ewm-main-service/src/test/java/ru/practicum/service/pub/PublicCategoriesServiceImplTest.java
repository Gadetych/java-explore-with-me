package ru.practicum.service.pub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.admin.CategoryDto;
import ru.practicum.model.Category;
import ru.practicum.repository.admin.AdminCategoriesRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.profiles.active=test")
@Transactional
class PublicCategoriesServiceImplTest {
    @Autowired
    private PublicCategoriesServiceImpl service;
    @Autowired
    private AdminCategoriesRepository repository;

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
        id1 = repository.save(model1).getId();
        id2 = repository.save(model2).getId();
    }

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
    void find_shouldReturnNotNull() {
        CategoryDto result = service.find(id1);

        assertNotNull(result);
        assertEquals(id1, result.getId());
        assertEquals(name1, result.getName());
    }
}