package com.agmerrizky.cosplayin.users.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @Size(min = 3, max = 255, message = "name must be beetween 3 and 255")
    private String fullName;

    private MultipartFile profilePicture;

    private MultipartFile bannerPicture;

    @Pattern(regexp = "^(?:\\+62|62|0)8[1-9][0-9]{6,10}$", message = "please provide a valid indonesian phone number")
    private String phoneNumber;

    private String description;

    @Pattern(regexp = "^(ADMIN|MODERATOR|USER)$", message = "invalid role type")
    private String role;

    private LocalDate birthday;

}
