package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.admin.NewUserRequest;
import ru.practicum.dto.admin.UserDto;
import ru.practicum.model.User;

@UtilityClass
public class AdminUsersMapper {
    public User dtoToModel(NewUserRequest dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserDto modelToDto(User model) {
        return UserDto.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .build();
    }
}
