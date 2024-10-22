package ssafy.sera.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ssafy.sera.api.member.response.UserDetailResponse;
import ssafy.sera.api.member.response.UserIdResponse;
import ssafy.sera.api.member.response.UserProfileDetailResponse;
import ssafy.sera.api.member.response.UserSignupResponse;
import ssafy.sera.common.constant.global.S3_IMAGE;
import ssafy.sera.common.exception.User.EmailDuplicateException;
import ssafy.sera.common.exception.User.PasswordNotMatchException;
import ssafy.sera.common.exception.security.InvalidPasswordException;
import ssafy.sera.common.exception.security.NotAuthenticatedException;
import ssafy.sera.common.exception.security.PasswordUsedException;
import ssafy.sera.common.exception.token.TokenSaveFailedException;
import ssafy.sera.common.util.JwtProcessor;
import ssafy.sera.common.util.S3Util;
import ssafy.sera.common.util.SecurityUtil;
import ssafy.sera.domain.member.command.MemberSignupCommand;
import ssafy.sera.domain.member.command.PasswordUpdateCommand;
import ssafy.sera.domain.member.command.UpdateProfileCommand;
import ssafy.sera.domain.member.common.PasswordHistory;
import ssafy.sera.domain.member.entity.User;
import ssafy.sera.domain.member.repository.UserRepository;
import ssafy.sera.common.exception.User.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;

    @Override
    @Transactional
    public UserSignupResponse signupMember(MemberSignupCommand signupCommand) {
        log.info("[UserService] 유저 회원가입");
        User existingUser = userRepository.findByEmail(signupCommand.email())
                .orElse(null);

        if (isDuplicateEmail(existingUser)) {
            throw new EmailDuplicateException();
        }

        User playerToSave = existingUser != null ? existingUser : createNewUser(signupCommand);
        MultipartFile imageFile = signupCommand.imageUrl();
        String imageUrl = handleProfileImage(imageFile, playerToSave.getId(), playerToSave.getImage());

        String encodedPassword = passwordEncoder.encode(signupCommand.password());
        playerToSave.signupMember(signupCommand, imageUrl, encodedPassword);
        userRepository.save(playerToSave);

        try {
            String accessToken = jwtProcessor.generateAccessToken(playerToSave);
            String refreshToken = jwtProcessor.generateRefreshToken(playerToSave);
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
        String preSignedProfileImage = generatePreSignedUrl(currentUser.getImage());

        return UserDetailResponse.of(
                preSignedProfileImage,
                currentUser.getNickname()
        );
    }

    @Override
    public UserProfileDetailResponse getMemberProfileDetail() {
        log.info("[UserService] 유저 상세 프로필 조회");
        User currentUser = getCurrentLoggedInMember();
        String preSignedProfileImage = generatePreSignedUrl(currentUser.getImage());

        return UserProfileDetailResponse.of(
                preSignedProfileImage,
                currentUser.getNickname(),
                currentUser.getGender(),
                currentUser.getNumber(),
                currentUser.getEmail(),
                currentUser.getDescription()
        );
    }

    @Override
    @Transactional
    public UserDetailResponse updateMemberProfile(UpdateProfileCommand command) {
        log.info("[UserService] 유저 정보 변경");
        User currentUser = getCurrentLoggedInMember();

        MultipartFile profileImageFile = command.profileImagePath();
        String imageUrl = S3_IMAGE.DEFAULT_URL;
        if (!command.deleteImage()) {
            imageUrl = handleProfileImage(profileImageFile, currentUser.getId(), currentUser.getImage());
        }// MultipartFile로 변경
        String preSignedUrl = generatePreSignedUrl(imageUrl);

        String updatedNickname = getUpdatedField(command.nickname(), currentUser.getNickname());
        String updatedDescription = getUpdatedField(command.description(), currentUser.getDescription());

        currentUser.updateProfile(updatedNickname, imageUrl, updatedDescription);
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
        boolean isDuplicated = userRepository.existsByNickname(nickname);
        log.debug("[UserService] >>>> 닉네임: {}, 중복 여부: {}", nickname, isDuplicated);
        return isDuplicated;
    }

    @Override
    @Transactional
    public void updateMemberPassword(PasswordUpdateCommand command) {
        log.info("[UserService] 비밀번호 변경");
        User player = getUserForPasswordUpdate(command);

        verifyCurrentPassword(command.currentPassword(), player);
        verifyNewPassword(command.newPassword(), player);

        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        player.updatePassword(encodedNewPassword);
        userRepository.save(player);
    }

    private boolean isDuplicateEmail(User existingUser) {
        return existingUser != null && !existingUser.getIsDeleted();
    }

    private User createNewUser(MemberSignupCommand command) {
        return User.builder()
                .email(command.email())
                .gender(command.gender())
                .birth(command.birth())
                .nickname(command.nickname())
                .password(passwordEncoder.encode(command.password()))
                .number(command.number())
                .description(command.description())
                .rating(0)
                .build();
    }

    private String handleProfileImage(MultipartFile imageFile, Long playerId, String existingImageUrl) {
        return s3Util.uploadImageToS3(imageFile, playerId, "profileImg/", existingImageUrl);
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
        User player = userRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);

        if (player.getIsDeleted()){
            throw new NotAuthenticatedException();
        }
        return player;
    }

    private User getUserForPasswordUpdate(PasswordUpdateCommand command) {
        if (command.email() != null) {
            return userRepository.findNotDeletedUserByEmail(command.email())
                    .orElseThrow(MemberNotFoundException::new);
        }
        return getCurrentLoggedInMember();
    }

    private void verifyCurrentPassword(String currentPassword, User player) {
        if (!passwordEncoder.matches(currentPassword, player.getPassword())) {
            throw new PasswordNotMatchException();
        }
    }

    private void verifyNewPassword(String newPassword, User player) {
        if (newPassword.length() < 8 || !newPassword.matches(".*[!@#\\$%^&*].*")) {
            throw new InvalidPasswordException();
        }

        for (PasswordHistory history : player.getPasswordHistories()) {
            if (passwordEncoder.matches(newPassword, history.getPassword())) {
                throw new PasswordUsedException();
            }
        }
    }

    public UserIdResponse getMemberId() {
        Long playerId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        return UserIdResponse.of(playerId);
    }
}
