package com.agmerrizky.cosplayin.users;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agmerrizky.cosplayin.common.anotations.CurrentUser;
import com.agmerrizky.cosplayin.common.anotations.RequireAuth;
import com.agmerrizky.cosplayin.common.anotations.RequireRole;
import com.agmerrizky.cosplayin.common.api.SuccessResponse;
import com.agmerrizky.cosplayin.common.entity.Users;
import com.agmerrizky.cosplayin.common.type.CurrentUserContext;
import com.agmerrizky.cosplayin.users.dto.UpdateUserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@RequireAuth
public class UsersController {

        private final UsersService service;

        @RequireRole("ADMIN")
        @GetMapping("/all")
        public ResponseEntity<SuccessResponse<List<Users>>> handleGetAllUsers() {
                List<Users> data = service.getAllUsers();
                return ResponseEntity.ok(
                                SuccessResponse.<List<Users>>builder()
                                                .message("im alive dawg")
                                                .data(data)
                                                .build());
        }

        @GetMapping("/my-profile")
        public ResponseEntity<SuccessResponse<Users>> handleGetMyProfiles(@CurrentUser CurrentUserContext curr) {
                Users data = service.getUserById(curr.id());
                return ResponseEntity.ok().body(
                                SuccessResponse.<Users>builder()
                                                .message("succesfully getting your data")
                                                .data(data)
                                                .build());
        }

        @PatchMapping("/my-profile")
        public ResponseEntity<SuccessResponse<Users>> handleUpdateMyProfiles(
                        @CurrentUser CurrentUserContext curr,
                        @Valid @ModelAttribute UpdateUserDto dto) {
                Users updatedData = service.updateUsers(dto, curr.id());

                return ResponseEntity.ok().body(
                                SuccessResponse.<Users>builder()
                                                .message("successfully update your data")
                                                .data(updatedData)
                                                .build());
        }

        @PatchMapping("/update/{id}")
        @RequireRole("ADMIN")
        public ResponseEntity<SuccessResponse<Users>> handleUpdateAnotheUserProfile(
                        @PathVariable UUID id,
                        @CurrentUser CurrentUserContext ctx,
                        @Valid @ModelAttribute UpdateUserDto dto) {

                Users updatedData = service.updateUsers(dto, ctx, id);
                return ResponseEntity.ok()
                                .body(SuccessResponse.<Users>builder()
                                                .message("successfully updated the user data")
                                                .data(updatedData)
                                                .build());
        }

        @DeleteMapping("/delete/{id}")
        @RequireRole("ADMIN")
        public ResponseEntity<SuccessResponse<Object>> handleDeleteUsers(
                        @PathVariable UUID id) {
                service.deleteUsers(id);
                return ResponseEntity.ok()
                                .body(SuccessResponse.builder()
                                                .message("successfully deleting the user's data")
                                                .data(null)
                                                .build());
        }

}
