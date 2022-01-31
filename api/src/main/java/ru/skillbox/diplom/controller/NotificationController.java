package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("api/v1/notifications")
@PreAuthorize("hasAuthority('developers:read')")
public interface NotificationController {

    @GetMapping
    ResponseEntity<?> getNotifications();

    @PutMapping
    ResponseEntity<?> changeAllStatus();

    @PutMapping(value = "/{id}")
    ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestParam String type);
}
