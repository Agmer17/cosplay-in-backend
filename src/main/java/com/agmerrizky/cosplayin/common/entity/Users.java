package com.agmerrizky.cosplayin.common.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import com.agmerrizky.cosplayin.common.type.OauthProvider;
import com.agmerrizky.cosplayin.common.type.UserRoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "name cannot be a blank")
    @Size(min = 3, max = 255, message = "name cannot be blank")
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email(message = "please provide a valid email!")
    private String email;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private OauthProvider oauthProvider;

    @NotNull
    @Column(nullable = false, updatable = false)
    private String oauthProviderId;

    @Column
    private String profilePicture;

    @Column
    private String bannerPicture;

    @Column
    @Pattern(regexp = "^(?:\\+62|62|0)8[1-9][0-9]{6,10}$", message = "please provide a valid indonesian phone number")
    private String phoneNumber;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private UserRoleType role;

    @Column
    private LocalDate birthday;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column
    @SQLRestriction(value = "deleted_at is NULL")
    private LocalDateTime deletedAt;
}
