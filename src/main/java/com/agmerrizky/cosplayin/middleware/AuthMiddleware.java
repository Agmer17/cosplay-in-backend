package com.agmerrizky.cosplayin.middleware;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.agmerrizky.cosplayin.common.security.JwtUtils;
import com.agmerrizky.cosplayin.users.dto.UsersSessionDto;
import com.agmerrizky.cosplayin.users.service.UsersService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMiddleware implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final UsersService usersService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String accessToken = null;

        // 1. Ambil array of cookies dari request
        Cookie[] cookies = request.getCookies();

        // 2. Cek apakah ada cookie yang dikirim, lalu cari yang namanya "access_token"
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken != null && !accessToken.trim().isEmpty()) {
            try {
                Claims claim = this.jwtUtils.parseToken(accessToken);
                String idStr = claim.getSubject();
                System.out.println("\n\n\n\n\n\n MIDDLEWARE ACCCESSING JWT ID WHICH IS : " + idStr);
                if (idStr != null) {
                    UUID id = UUID.fromString(idStr);
                    UsersSessionDto user = usersService.getUsersSessionData(id);
                    System.out.println("USER CLASS SESSIONNYA : " + user.getClass());

                    if (user != null) {
                        System.out.println("USER CLASS SESSIONNYA : " + user.getClass());
                        request.setAttribute("id", id);
                        request.setAttribute("role", user.role());
                        System.out.println("ROLE USERNYA SEKARANG : " + user.role());
                        request.setAttribute("status", user.status());
                    }

                }

            } catch (Exception e) {
                System.out.println("JWT Parsing Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return true;
    }
}