package com.agmerrizky.cosplayin.common.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.agmerrizky.cosplayin.common.anotations.RequireRole;
import com.agmerrizky.cosplayin.common.exceptions.ForbiddenAccessException;
import com.agmerrizky.cosplayin.common.type.UserRoleType;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequireAspectRole {

    @Around("@annotation(requireRole) && @within(org.springframework.web.bind.annotation.RestController)")
    public Object checkRoleOnMethod(ProceedingJoinPoint joinPoint,
            RequireRole requireRole) throws Throwable {
        return validate(joinPoint, requireRole);
    }

    // Handle @RequireRole di CLASS
    @Around("@within(requireRole) && @within(org.springframework.web.bind.annotation.RestController)")
    public Object checkRoleOnClass(ProceedingJoinPoint joinPoint,
            RequireRole requireRole) throws Throwable {
        return validate(joinPoint, requireRole);
    }

    private Object validate(ProceedingJoinPoint joinPoint,
            RequireRole requireRole) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        UserRoleType currentUserRole = (UserRoleType) request.getAttribute("role");
        String required = requireRole.value();

        if (!required.equals(currentUserRole.toString())) {
            throw new ForbiddenAccessException("you doesn't have access to this feature!");
        }

        return joinPoint.proceed();
    }
}
