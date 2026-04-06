package org.example.expert.domain.user.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Template s3Template;
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofHours(1);


    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    public List<UserResponse> getUsersByNickname(String nickname) {
        List<User> users = userRepository.findAllByNickname(nickname);
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            Long id = user.getId();
            String email = user.getEmail();
            String userNickname = user.getNickname();

            responses.add(new UserResponse(id, email, userNickname));
        }

        return responses;
    }
}

//    @Value("${spring.cloud.aws.s3.bucket}")
//    private String bucket;
//
//    @Transactional
//    public String upload(long userId, MultipartFile file) {
//
//        try {
//           User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("등록된 멤버가 없다."));
//            String key = "uploads/"+userId + UUID.randomUUID() + "_" + file.getOriginalFilename();
//            s3Template.upload(bucket, key, file.getInputStream());
//
//
//            user.updateProfileImageKey(key);
//
//            return key;
//
//        } catch (IOException e) {
//            throw new IllegalArgumentException("파일 업로드 실패", e);
//        }
//    }
//
//
//    @Transactional(readOnly = true)
//    public URL getDownloadUrl(Long userId) {
//
//        String key = userRepository.findById(userId)
//                .orElseThrow()
//                .getProfileImageKey();
//
//        return s3Template.createSignedGetURL(bucket, key,PRESIGNED_URL_EXPIRATION);
//    }
//    }

