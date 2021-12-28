package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RequestMapping("/api/v1/storage")
public interface StorageController {

    @PostMapping()
    ResponseEntity<?> storage(@RequestParam("file")MultipartFile file);

}
