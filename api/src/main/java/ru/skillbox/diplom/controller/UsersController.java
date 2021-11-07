package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;

@CrossOrigin
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('developers:read')")
public interface UsersController {

    @GetMapping("/me")
    CommonResponse<PersonDto> getProfile();
}
