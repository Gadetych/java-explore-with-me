package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UsersService {
    List<UserDto> findAllUsers(List<Long> array, int from, int size);

    UserDto createUser(NewUserRequest requestBody);

    void deleteUser(long userId);
}
