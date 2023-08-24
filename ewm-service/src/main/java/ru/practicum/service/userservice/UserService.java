package ru.practicum.service.userservice;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    void delete(long id);

    List<UserDto> getAll(List<Long> ids, int from, int size);
}
