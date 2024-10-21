package ssafy.sera.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.sera.common.constant.global.RATING;
import ssafy.sera.common.constant.global.S3_IMAGE;
import ssafy.sera.domain.member.command.MemberSignupCommand;
import ssafy.sera.domain.member.common.BaseMemberEntity;
import ssafy.sera.domain.member.common.Gender;
import ssafy.sera.domain.member.common.Member;
import ssafy.sera.domain.member.common.MemberRole;
import ssafy.sera.domain.member.common.PasswordHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)  // public 기본 생성자
@AllArgsConstructor(access = AccessLevel.PROTECTED)  // 모든 필드를 포함한 생성자 (protected)
public class User extends BaseMemberEntity implements Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String image;

    private String number;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birth;

    private String description;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordHistory> passwordHistories = new ArrayList<>();

    @Builder
    public User(String email, String password, String nickname, String image, String number, Gender gender, LocalDate birth, String description, Integer rating, int winStreak, int loseStreak, int gameCount) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.number = number;
        this.gender = gender;
        this.birth = birth;
        this.description = description;
        this.passwordHistories = new ArrayList<>();
        this.role = MemberRole.PLAYER;
        this.isDeleted = false;
    }

    // 명시적인 생성자 추가 (null 값 허용)
    public User(Integer rating, MemberRole role) {
        this.isDeleted = false;
        this.role = role;
    }

    public static User createTempPlayer() {
        return new User(RATING.INITIAL_RATING, MemberRole.TEMP);
    }

    public void signupMember(MemberSignupCommand signupCommand, String imageUrl, String password) {
        this.email = signupCommand.email();
        this.password = password;
        this.nickname = signupCommand.nickname();
        this.image = imageUrl;
        this.number = signupCommand.number();
        this.gender = signupCommand.gender();
        this.birth = signupCommand.birth();
        this.description = signupCommand.description();
        this.isDeleted = false;
        this.role = MemberRole.PLAYER;
    }

    public void updateProfile(String nickname, String profileImagePath, String description) {
        this.nickname = nickname;
        this.image = profileImagePath;
        this.description = description;
    }

    public void addPasswordHistory(String password) {
        PasswordHistory passwordHistory = PasswordHistory.builder()
                .player(this)
                .password(password)
                .build();
        this.passwordHistories.add(passwordHistory);
    }

    // 비밀번호 변경 전에 이력을 저장
    public void updatePassword(String newPassword) {
        this.addPasswordHistory(this.password);
        this.password = newPassword;
    }

    // 저장 전에 기본 role 설정
    @Override
    public void prePersist() {
        if (image == null){
            image = S3_IMAGE.DEFAULT_URL;
        }
    }
}
