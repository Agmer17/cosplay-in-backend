package com.agmerrizky.cosplayin.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.auth.service.AuthService;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthService authService;

        @GetMapping("/google")
        public ResponseEntity<Void> handleGoogleLogin() {
                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .location(URI.create(authService.getGoogleLoginUrl()))
                                .build();
        }

        @GetMapping("/google-callback")
        public ResponseEntity<SuccessResponse<String>> handleGoogleCallback(@RequestParam String code) {
                String accessToken = authService.handleGoogleCallback(code);

                ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(604800)
                                .sameSite("lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(
                                                SuccessResponse.<String>builder()
                                                                .message("successfully logged in with google")
                                                                .data(accessToken)
                                                                .build());
        }

        @GetMapping("/logout")
        public ResponseEntity<SuccessResponse<Object>> handleLogout() {
                ResponseCookie cookie = ResponseCookie.from("access_token", "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0)
                                .sameSite("lax")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(
                                                SuccessResponse.builder()
                                                                .message("successfully logout")
                                                                .data(null)
                                                                .build());
        }

}
