package com.example.jobmanagementsystem.controller;

import com.example.jobmanagementsystem.dto.CreateUserRequest;
import com.example.jobmanagementsystem.dto.UpdateUserRequest;
import com.example.jobmanagementsystem.dto.UserDto;
import com.example.jobmanagementsystem.entity.User;
import com.example.jobmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Manual DTO to Entity mapping
    private User convertToEntity(CreateUserRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Password will be encoded by service
        user.setRole(dto.getRole());
        return user;
    }

    // Manual Entity to DTO mapping
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User userToCreate = convertToEntity(createUserRequest);
        User createdUser = userService.createUser(userToCreate);
        return new ResponseEntity<>(convertToDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(convertToDto(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(convertToDto(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        // Note: Email path variable might need URL encoding if it contains special characters.
        // For simplicity, assuming basic email formats.
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(convertToDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        // For update, we pass the DTO to the service, and the service handles partial updates.
        // The service layer will fetch the existing entity and apply changes.
        // This controller method needs to map UpdateUserRequest to a User entity fragment.
        User userDetails = new User(); // Create a temporary User object to hold updates
        userDetails.setUsername(updateUserRequest.getUsername());
        userDetails.setEmail(updateUserRequest.getEmail());
        userDetails.setPassword(updateUserRequest.getPassword()); // Password updated if not null/empty in service
        userDetails.setRole(updateUserRequest.getRole());

        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
