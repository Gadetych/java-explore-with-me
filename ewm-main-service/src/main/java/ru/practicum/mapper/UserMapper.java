package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {
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

    public UserShortDto modelToUserShortDto(User model) {
        return UserShortDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public User shortDtoToModel(UserShortDto model) {
        return User.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}
