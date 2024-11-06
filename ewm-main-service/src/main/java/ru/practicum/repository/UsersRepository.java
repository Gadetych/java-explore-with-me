package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.User;

import java.util.List;

public interface UsersRepository extends JpaRepository<User, Long> {
    boolean existsById(Long id);

    @Query("SELECT u FROM User u ORDER BY u.id LIMIT :size OFFSET :from")
    List<User> findAllLimit(@Param("from") int from, @Param("size") int size);
}
