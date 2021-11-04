package ru.skillbox.diplom.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@PreAuthorize("hasAuthority('developers:read')")
@RequestMapping("/api/v1/developers")
public interface TestController {

    @GetMapping
    List<Developer> getAll();

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:read')")
    Developer getById(@PathVariable Long id);

    @PostMapping
    @PreAuthorize("hasAuthority('developers:write')")
    Developer create(@RequestBody Developer developer);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    void deleteById(@PathVariable Long id);

    @Data
    @AllArgsConstructor
    class Developer {
        private Long id;
        private String firstName;
        private String lastName;
    }
}
