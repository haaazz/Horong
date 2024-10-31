package ssafy.horong.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.member.response.UserDetailResponse;
import ssafy.horong.api.member.response.UserIdResponse;
import ssafy.horong.api.member.response.UserProfileDetailResponse;
import ssafy.horong.api.member.response.UserSignupResponse;
import ssafy.horong.common.constant.global.S3_IMAGE;
import ssafy.horong.common.exception.User.UserIdDuplicateException;
import ssafy.horong.common.exception.User.PasswordNotMatchException;
import ssafy.horong.common.exception.security.InvalidPasswordException;
import ssafy.horong.common.exception.security.NotAuthenticatedException;
import ssafy.horong.common.exception.security.PasswordUsedException;
import ssafy.horong.common.exception.token.TokenSaveFailedException;
import ssafy.horong.common.util.JwtProcessor;
import ssafy.horong.common.util.S3Util;
import ssafy.horong.common.util.SecurityUtil;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.PasswordHistory;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.PasswordHistoryRepository;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.common.exception.User.*;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;
    private final PasswordHistoryRepository passwordHistoryRepository;

    @Override
    @Transactional
    public UserSignupResponse signupMember(MemberSignupCommand signupCommand) {

        validateSignupCommand(signupCommand);

        log.info("[UserService] 유저 회원가입");

        User userToSave = createNewUser(signupCommand);
        String encodedPassword = passwordEncoder.encode(signupCommand.password());
        userToSave.signupMember(signupCommand,encodedPassword, signupCommand.language());
        userRepository.save(userToSave);
        MultipartFile imageFile = signupCommand.imageUrl();
        String imageUrl = handleProfileImage(imageFile, userToSave.getId(), userToSave.getProfileImg());
        userToSave.setProfileImg(imageUrl);
        userRepository.save(userToSave);
        passwordHistoryRepository.save(PasswordHistory.builder()
                .user(userToSave)
                .password(encodedPassword)
                .build());

        try {
            String accessToken = jwtProcessor.generateAccessToken(userToSave);
            String refreshToken = jwtProcessor.generateRefreshToken(userToSave);
            jwtProcessor.saveRefreshToken(accessToken, refreshToken);
            return UserSignupResponse.of(accessToken, refreshToken);
        } catch (Exception e) {
            throw new TokenSaveFailedException();
        }
    }

    @Override
    public UserDetailResponse getMemberDetail() {
        log.info("[UserService] 유저 정보 조회");
        User currentUser = getCurrentLoggedInMember();
        String preSignedProfileImage = generatePreSignedUrl(currentUser.getProfileImg());

        return UserDetailResponse.of(
                preSignedProfileImage,
                currentUser.getNickname()
        );
    }

    @Override
    public UserProfileDetailResponse getMemberProfileDetail() {
        log.info("[UserService] 유저 상세 프로필 조회");
        User currentUser = getCurrentLoggedInMember();
        String preSignedProfileImage = generatePreSignedUrl(currentUser.getProfileImg());

        return UserProfileDetailResponse.of(
                preSignedProfileImage,
                currentUser.getNickname()
        );
    }

    @Override
    @Transactional
    public UserDetailResponse updateMemberProfile(UpdateProfileCommand command) {
        log.info("[UserService] 유저 정보 변경");

        validateUpdateProfileCommand(command);
        User currentUser = getCurrentLoggedInMember();

        MultipartFile profileImageFile = command.profileImagePath();
        String imageUrl = S3_IMAGE.DEFAULT_URL;
        if (!command.deleteImage()) {
            imageUrl = handleProfileImage(profileImageFile, currentUser.getId(), currentUser.getProfileImg());
        }// MultipartFile로 변경`
        String preSignedUrl = generatePreSignedUrl(imageUrl);

        String updatedNickname = getUpdatedField(command.nickname(), currentUser.getNickname());

        currentUser.updateProfile(updatedNickname, imageUrl);
        userRepository.save(currentUser);

        return UserDetailResponse.of(
                preSignedUrl,
                currentUser.getNickname()
        );
    }

    @Override
    @Transactional
    public String deleteMember() {
        log.info("[UserService] 유저 탈퇴");
        User currentUser = getCurrentLoggedInMember();
        currentUser.delete();
        userRepository.save(currentUser);
        return "회원 탈퇴가 성공적으로 처리되었습니다.";
    }

    @Override
    public boolean checkNickname(String nickname) {
        log.info("[UserService] 닉네임 중복 체크");
        if (isDuplicateNickname(nickname)) {
            throw new NickNameDuplicateException();
        }
        boolean isDuplicated = userRepository.existsByNickname(nickname);
        log.debug("[UserService] >>>> 닉네임: {}, 중복 여부: {}", nickname, isDuplicated);
        return isDuplicated;
    }

    @Override
    public boolean checkUserId(String userId) {
        if (isDuplicateUserId(userId)) {
            throw new UserIdDuplicateException();
        }
        log.info("[UserService] 아이디 중복 체크");
        boolean isDuplicated = userRepository.existsByUserId(userId);
        log.debug("[UserService] >>>> 아이디: {}, 중복 여부: {}", userId, isDuplicated);
        return isDuplicated;
    }

    @Override
    @Transactional
    public void updateMemberPassword(PasswordUpdateCommand command) {
        log.info("[UserService] 비밀번호 변경");
        User user = getUserForPasswordUpdate(command);

        verifyCurrentPassword(command.currentPassword(), user);
        verifyNewPassword(command.newPassword(), user);

        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        user.updatePassword(encodedNewPassword);

        PasswordHistory passwordHistory = PasswordHistory.builder()
                .user(user)
                .password(encodedNewPassword)
                .build();

        passwordHistoryRepository.save(passwordHistory);

        userRepository.save(user);
    }

    private boolean isDuplicateUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElse(null);

        return user != null && !user.isDeleted();
    }

    private boolean isDuplicateNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElse(null);

        return user != null && !user.isDeleted();
    }

    private User createNewUser(MemberSignupCommand command) {
        return User.builder()
                .nickname(command.nickname())
                .password(passwordEncoder.encode(command.password()))
                .build();
    }

    private String handleProfileImage(MultipartFile imageFile, Long userId, String existingImageUrl) {
        return s3Util.uploadUserImageToS3(imageFile, userId, "profileImg/", existingImageUrl);
    }

    private String generatePreSignedUrl(String imageUrl) {
        if (imageUrl == null) {
            return "";
        }
        String imagePath = extractImagePath(imageUrl);
        return s3Util.getPresignedUrlFromS3(imagePath);
    }

    private String extractImagePath(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf("profileImg/"));
    }

    private String getUpdatedField(String newValue, String currentValue) {
        return (newValue == null || newValue.isEmpty()) ? currentValue : newValue;
    }

    private User getCurrentLoggedInMember() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);

        if (user.isDeleted()){
            throw new NotAuthenticatedException();
        }
        return user;
    }

    private User getUserForPasswordUpdate(PasswordUpdateCommand command) {
        if (command.email() != null) {
            return userRepository.findNotDeletedUserByUserId(command.email())
                    .orElseThrow(MemberNotFoundException::new);
        }
        return getCurrentLoggedInMember();
    }

    private void verifyCurrentPassword(String currentPassword, User user) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordNotMatchException();
        }
    }

    private void verifyNewPassword(String newPassword, User user) {
        if (newPassword.length() < 8 || 20 < newPassword.length() || !newPassword.matches(".*[!@#$%^&*].*")) {
            throw new InvalidPasswordException();
        }

        for (PasswordHistory history : passwordHistoryRepository.getHistoriesByUserId(user.getId())) {
            if (passwordEncoder.matches(newPassword, history.getPassword()) && history.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                throw new PasswordUsedException();
            }
        }

    }

    public UserIdResponse getMemberId() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        return UserIdResponse.of(userId);
    }

    public void validateSignupCommand(MemberSignupCommand command) {
        if (command.userId().length() < 2 || command.userId().length() > 16) {
            throw new UserIdNotValidException();
        }
        if (!command.userId().matches("^[a-zA-Z0-9]+$")) {
            throw new NotAllowedUseridException();
        }
        if (command.password().length() < 8 || command.password().length() > 20) {
            throw new PasswordNotValidExeption();
        }
        if (!command.password().matches(".*[!@#$%^&*].*")){
            throw new InvalidPasswordException();
        }
        if (command.nickname().length() < 2 || command.nickname().length() > 20) {
            throw new NicknameNotValidExeption();
        }
        if (!command.nickname().matches("^[a-zA-Z0-9가-힣一-亜\u4e00-\u9fa5]+$")) {
            throw new NotAllowedNicknameException();
        }
        if (!isValidLanguage(command.language())) {
            throw new LanguageNotValidExeption();
        }
        if (isDuplicateUserId(command.userId())) {
            throw new UserIdDuplicateException();
        }
        if (isDuplicateNickname(command.nickname())) {
            throw new NickNameDuplicateException();
        }
    }

    public void validateUpdateProfileCommand(UpdateProfileCommand command) {
        if (command.nickname() != null) {
            if (command.nickname().length() < 2 || command.nickname().length() > 20) {
                throw new NicknameNotValidExeption();
            }
            if (isDuplicateNickname(command.nickname())) {
                throw new NickNameDuplicateException();
            }
            if (!command.nickname().matches("^[a-zA-Z0-9가-힣一-亜\u4e00-\u9fa5]+$")) {
                throw new NotAllowedNicknameException();
            }
        }
        if (command.language() != null) {
            if (!isValidLanguage(command.language())) {
                throw new LanguageNotValidExeption();
            }
        }
    }

    private boolean isValidLanguage(Language language) {
        for (Language lang : Language.values()) {
            if (lang == language) {
                return true;
            }
        }
        return false;
    }
}
