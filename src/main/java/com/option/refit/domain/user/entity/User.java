package com.option.refit.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "email_id", nullable = false, unique = true, length = 255)
    private String emailId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role", updatable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @Builder
    public User(String emailId, String name, LocalDate birthDate, Role role, String profileImageUrl) {
        this.emailId = emailId;
        this.name = name;
        this.birthDate = birthDate;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String name) {
        this.name = name;
    }

    public void changeProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 탈퇴: 이메일을 해제해서 같은 이메일로 다시 가입할 수 있게 한다 (이메일 컬럼은 unique)
    public void delete() {
        this.deletedAt = LocalDate.now();
        releaseEmail();
    }

    // 탈퇴 계정의 이메일을 비식별 값으로 바꾼다. 이메일이 해제되기 전에 탈퇴한 기존 계정에도 쓴다
    public void releaseEmail() {
        this.emailId = "deleted_" + this.id + "@deleted.invalid";
    }
}
