package ru.practicum.service.userservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.mapper.UserMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Пользователь с id={} успешно добавлен", user.getId());

        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Пользователь с id={} успешно удален", id);
        } else throw new NotFoundException("User with id=" + id + " was not found");
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<UserDto> result = userRepository.findAllByIdIn(ids, pageRequest).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Получен список пользователей под id {}", ids);
        return result;
    }
}
