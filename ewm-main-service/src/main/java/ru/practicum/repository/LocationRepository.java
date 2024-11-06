package ru.practicum.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.model.Location;

public interface LocationRepository extends CrudRepository<Location, Long> {
}
