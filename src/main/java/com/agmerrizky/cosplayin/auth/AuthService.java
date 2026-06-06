package com.agmerrizky.cosplayin.auth;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.agmerrizky.cosplayin.auth.dto.AuthGoogleCallbackDto;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.exceptions.FatalError;
import com.agmerrizky.cosplayin.common.type.OauthProvider;
import com.agmerrizky.cosplayin.users.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import com.agmerrizky.cosplayin.common.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuth20Service googleOauth;
    private final ObjectMapper mapper;
    private final UsersService userService;
    private final JwtUtils jwtUtils;

    public String getGoogleLoginUrl() {
        return this.googleOauth.getAuthorizationUrl();
    }

    public String handleGoogleCallback(String code) {
        OAuth2AccessToken accessToken;
        try {
            accessToken = this.googleOauth.getAccessToken(code);
        } catch (Exception e) {
            throw new FatalError("Cannot authorize the google login, try again another time");
        }

        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo");

        try {
            this.googleOauth.signRequest(accessToken, request);
        } catch (Exception e) {
            throw new FatalError("something wrong while trying to login with google please try again another time");
        }

        // todo implement access token json jwt
        try (Response response = this.googleOauth.execute(request)) {
            String jsonData = response.getBody();
            AuthGoogleCallbackDto authData = mapper.readValue(jsonData, AuthGoogleCallbackDto.class);

            Users user = userService.GetByOauthProviderId(OauthProvider.GOOGLE, authData.sub());

            if (user == null) {
                user = userService.createUser(authData.givenName(), authData.email(), OauthProvider.GOOGLE,
                        authData.sub());
            }

            String cookieAccessToken = jwtUtils.generateToken(user.getId(), user.getRole().toString());

            return cookieAccessToken;
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new FatalError("something wrong while trying to login with google, please try again another time : "
                    + e.getMessage());
        }
    }

}
