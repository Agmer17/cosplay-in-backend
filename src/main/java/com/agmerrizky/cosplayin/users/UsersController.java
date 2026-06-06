package com.agmerrizky.cosplayin.users;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.anotations.RequireRole;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@RequireAuth
public class UsersController {

    private final UsersService service;

    @RequireRole("ADMIN")
    @GetMapping("/all")
    public ResponseEntity<SuccessResponse<List<Users>>> getAllUsers() {
        List<Users> data = service.getAllUsers();
        return ResponseEntity.ok(
                SuccessResponse.<List<Users>>builder()
                        .message("im alive dawg")
                        .data(data)
                        .build());
    }

    @GetMapping("/my-profile")
    public ResponseEntity<SuccessResponse<Users>> getMethodName(@CurrentUser CurrentUserContext curr) {
        Users data = service.getUserById(curr.id());
        return ResponseEntity.ok().body(
                SuccessResponse.<Users>builder()
                        .message("succesfully getting your data")
                        .data(data)
                        .build());
    }

}
