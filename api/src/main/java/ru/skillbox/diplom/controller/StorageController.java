package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RequestMapping("/api/v1/storage")
@PreAuthorize("hasAuthority('developers:read')")
public interface StorageController {

    @PostMapping()
    ResponseEntity<?> storage(@RequestParam(value = "file", required = false) MultipartFile file);
}
