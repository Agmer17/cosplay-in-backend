package com.agmerrizky.cosplayin.common.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.exceptions.UnathorizedAccessException;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequireAuthAspect {

    @Before("@annotation(requireAuth) && @within(org.springframework.web.bind.annotation.RestController)")
    public void authenticate(JoinPoint joinPoint, RequireAuth requireAuth) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        Object id = request.getAttribute("id");

        if (id == null) {
            throw new UnathorizedAccessException("You need to login to access this feature");
        }
    }
}
