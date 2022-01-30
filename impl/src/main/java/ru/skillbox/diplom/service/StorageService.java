package ru.skillbox.diplom.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.repository.PersonRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class StorageService {

    private final Cloudinary cloudinary;
    private final PersonRepository personRepository;

    public StorageService(Cloudinary cloudinary, PersonRepository personRepository) {
        this.cloudinary = cloudinary;
        this.personRepository = personRepository;
    }

    @Transactional
    public ResponseEntity<?> saveImage(MultipartFile file) {
        if (file == null) return ResponseEntity.ok("ok");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );
        if(!file.isEmpty()) {
            log.info(String.format("Avatar has been saved for account %s", person.getEmail()));
            try {
                File coverImage = Files.createTempFile("tempCover", file.getOriginalFilename()).toFile();
                file.transferTo(coverImage);
                Map uploadResult = cloudinary.uploader().upload(coverImage, ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("url");
                System.out.println(url);
                person.setPhoto(url);
            } catch (IOException exception) {
                exception.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok("ok");
    }

}
