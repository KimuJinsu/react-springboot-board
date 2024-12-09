package com.example.backendboard.service;

import com.example.backendboard.dto.UserDTO;
import com.example.backendboard.entity.User;
import com.example.backendboard.repository.UserRepository;
import com.example.backendboard.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    public void register(UserDTO userDTO, MultipartFile profileImage) throws Exception {
        System.out.println("Registering User: " + userDTO);
        System.out.println("Profile Image Name: " + profileImage.getOriginalFilename());
        if (!userDTO.getPassword().equals(userDTO.getCheckPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String profileImageUrl = s3Service.uploadFile(profileImage);

        User user = new User();
        user.setUsername(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 비밀번호 암호화
        user.setProfileImageUrl(profileImageUrl);
        user.setVerified(false);

        // 사용자 저장
        userRepository.save(user);
    }
}