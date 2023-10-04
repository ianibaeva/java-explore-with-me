package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.user.mapper.UserMapper.toUser;
import static ru.practicum.ewm.user.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        String name = newUserRequest.getName();

        if (userRepository.existsByName(name)) {
            throw new ConflictException(
                    "Username already exists"
            );
        }

        User user = toUser(newUserRequest);

        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from, size);

        List<User> users = userRepository.getAllUsersByIdInOrPageable(ids, pageable);

        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new ObjectNotFoundException(String.format(
                    "User with ID: %s not found", userId
            ));
        }
    }
}
