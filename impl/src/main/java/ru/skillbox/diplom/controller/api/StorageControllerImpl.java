package ru.skillbox.diplom.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.diplom.controller.StorageController;
import ru.skillbox.diplom.service.StorageService;

@RestController
public class StorageControllerImpl implements StorageController {

    StorageService storageService;

    public StorageControllerImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public ResponseEntity<?> storage(MultipartFile file) {
        return storageService.saveImage(file);
    }

}
