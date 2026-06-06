package com.agmerrizky.cosplayin.middleware;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.agmerrizky.cosplayin.common.security.JwtUtils;
import com.agmerrizky.cosplayin.common.type.UserRoleType;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMiddleware implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

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
                String roleStr = claim.get("role", String.class);

                if (idStr != null && roleStr != null) {
                    UUID id = UUID.fromString(idStr);
                    UserRoleType role = UserRoleType.valueOf(roleStr);

                    request.setAttribute("id", id);
                    request.setAttribute("role", role);
                }

            } catch (Exception e) {
                System.out.println("JWT Parsing Error: " + e.getMessage());
            }
        }

        return true;
    }
}