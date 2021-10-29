package ru.skillbox.diplom.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.response.loginResponse.InvalidLoginResponse;
import ru.skillbox.diplom.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        try {
            return ResponseEntity.ok(userService.getProfileData());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new InvalidLoginResponse(), HttpStatus.BAD_REQUEST);
        }
    }
}
