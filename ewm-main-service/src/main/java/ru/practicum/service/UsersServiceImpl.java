package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.conflict.UniqueEmailByUserException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UsersRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository repository;

    //    Admin
    @Override
    public List<UserDto> findAllUsers(List<Long> array, int from, int size) {
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

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest requestBody) {
        log.debug("==> Creating new user: {}", requestBody);
        if (repository.existsByEmail(requestBody.getEmail())) {
            throw new UniqueEmailByUserException("Email already exists");
        }
        User model = repository.save(UserMapper.dtoToModel(requestBody));
        log.debug("<== Created new user: {}", model);
        return UserMapper.modelToDto(model);
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        log.debug("==> Deleting user: {}", userId);
        repository.deleteById(userId);
        log.debug("<== Deleted user: {}", userId);
    }
}
