package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
//import org.example.expert.domain.common.annotation.Auth;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.FileDownloadUrlResponse;
import org.example.expert.domain.user.dto.response.FileUploadResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // 도전 과제 13번 api 구현.
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsersByNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.getUsersByNickname(nickname));
    }



    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }


//    // 유저 프로필 생성
//    @PostMapping("/users/{userId}/profile")
//    public ResponseEntity<FileUploadResponse> createUserProfile(
//            @PathVariable Long userId,
//            @RequestParam("file") MultipartFile file
//    ){
//        String key = userService.upload(userId, file);
//        return ResponseEntity.ok(new FileUploadResponse(key));
//    }
//
//
//    // 유저 사진 조회
//    @GetMapping("/users/{userId}/profile")
//    public ResponseEntity<FileDownloadUrlResponse> getDownloadUrl(@PathVariable Long userId) {
//        URL url = userService.getDownloadUrl(userId);
//        return ResponseEntity.ok(new FileDownloadUrlResponse(url.toString()));
//    }


}
