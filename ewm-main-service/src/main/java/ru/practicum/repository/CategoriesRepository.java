package ru.practicum.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Category;

import java.util.List;

public interface CategoriesRepository extends CrudRepository<Category, Long> {
    @Query("SELECT c FROM Category c ORDER BY c.id LIMIT :size OFFSET :from")
    List<Category> findAllLimit(@Param("from") int from, @Param("size") int size);

    boolean existsByName(String name);

    Category findByName(String name);
}
