package com.example.backendboard.controller;

import com.example.backendboard.dto.UserDTO;
import com.example.backendboard.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    @PostMapping("/registers")
    public ResponseEntity<String> registerUser(
            @RequestPart("data") UserDTO userDTO,
            @RequestPart("image") MultipartFile profileImage) {
        System.out.println("Received UserDTO: " + userDTO);
        System.out.println("Profile Image: " + profileImage.getOriginalFilename());
        try {
            userRegisterService.register(userDTO, profileImage);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}