package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UsersRepository;
import ru.practicum.service.AdminUsersService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminUsersServiceImpl implements AdminUsersService {
    private final UsersRepository repository;

    @Override
    public List<UserDto> findAllUsers(List<Long> array, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<User> users;
        if (array == null || array.isEmpty()) {
            users = repository.findAllLimit(from, size);
        } else {
            users = repository.findAllById(array);
        }
        return users.stream()
                .map(UserMapper::modelToDto)
                .toList();
    }

    @Override
    public UserDto createUser(NewUserRequest requestBody) {
        log.debug("==> Creating new user: {}", requestBody);
        User model = repository.save(UserMapper.dtoToModel(requestBody));
        log.debug("<== Creating new user: {}", model);
        return UserMapper.modelToDto(model);
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("==> Deleting user: {}", userId);
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User not found by id: " + userId);
        }
        repository.deleteById(userId);
        log.debug("<== Deleting user: {}", userId);
    }
}
