package com.agmerrizky.cosplayin.common.resolver;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.exceptions.ForbiddenAccessException;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.common.type.UserRoleType;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Object objectId = request.getAttribute("id");
        Object objectRole = request.getAttribute("role");

        System.out.println("\n\n\n\n\n\n\nOBJECT ID NYA : " + (UUID) objectId);

        if (objectId == null || objectRole == null) {
            throw new ForbiddenAccessException("you need to login to access this feature!");
        }

        UUID userId = (UUID) objectId;
        UserRoleType role = (UserRoleType) objectRole;
        System.out.println("\n\n\n\n\n\n\nOBJECT role NYA : " + role);

        return new CurrentUserContext(userId, role);

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(CurrentUserContext.class);
    }

}
