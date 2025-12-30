package hh.inpt.smet.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hh.inpt.smet.user.dto.UserDTO;
import hh.inpt.smet.user.mapper.UserMapper;
import hh.inpt.smet.user.model.UserEntity;
import hh.inpt.smet.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO.PostOutput> getAllUsers() {
        return userService.getUsers()
                .stream()
                .map(UserMapper::toOutput)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDTO.PostOutput getUserById(@PathVariable Long id) {
        UserEntity user = userService.getUserById(id);
        return UserMapper.toOutput(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO.PostOutput> createUser(
            @RequestBody UserDTO.PostInput input) {

        UserEntity entity = UserMapper.toEntity(input);
        UserEntity saved = userService.cerateUser(entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.toOutput(saved));
    }

    @PutMapping("/{id}")
    public UserDTO.PostOutput updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO.PostInput input) {

        UserEntity entity = UserMapper.toEntity(input);
        UserEntity updated = userService.updateUser(id, entity);

        return UserMapper.toOutput(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
