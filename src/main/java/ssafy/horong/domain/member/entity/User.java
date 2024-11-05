package ssafy.horong.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.common.constant.global.S3_IMAGE;
import ssafy.horong.domain.community.entity.Post;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.MemberRole;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)  // public 기본 생성자
@AllArgsConstructor(access = AccessLevel.PROTECTED)  // 모든 필드를 포함한 생성자 (protected)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String userId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 66)
    private String password; // 비밀번호는 8~20자까지 설정 가능

    @Column(length = 40)
    private String profileImg; // s3 링크 저장

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language; // enum 타입

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> boards;

    @Builder
    public User(String password, String nickname, String image) {
        this.password = password;
        this.nickname = nickname;
        this.profileImg = image;
        this.isDeleted = false;
    }

    // 명시적인 생성자 추가 (null 값 허용)
    public User(MemberRole role) {
        this.isDeleted = false;
        this.role = role;
    }

    public void signupMember(MemberSignupCommand signupCommand,  String password, Language language) {
        this.password = password;
        this.nickname = signupCommand.nickname();
        this.userId = signupCommand.userId();
        this.isDeleted = false;
        this.role = MemberRole.USER;
        this.language = language;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void delete() {
        this.isDeleted = true;
    }

    // 저장 전에 기본 role 설정
    @PrePersist
    public void prePersist() {
        if (profileImg == null) {
            profileImg = S3_IMAGE.DEFAULT_URL;
        }
        createdAt = LocalDateTime.now();
    }
}