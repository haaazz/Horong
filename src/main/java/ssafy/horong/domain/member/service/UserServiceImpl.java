package ssafy.horong.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.horong.api.member.response.*;
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
import ssafy.horong.domain.education.entity.EducationDay;
import ssafy.horong.domain.education.repository.EducationDayRepository;
import ssafy.horong.domain.education.repository.EducationStampRepository;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;
import ssafy.horong.domain.member.common.Language;
import ssafy.horong.domain.member.common.PasswordHistory;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.member.repository.PasswordHistoryRepository;
import ssafy.horong.domain.member.repository.UserRepository;
import ssafy.horong.common.exception.User.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final RedisTemplate<String, String> redisTemplateslang;
    private static final String FORBIDDEN_WORDS_KEY = "forbiddenWords";
    private final EducationDayRepository educationDayRepository;
    private final EducationStampRepository educationStampRepository;

    @Override
    @Transactional
    public UserSignupResponse signupMember(MemberSignupCommand signupCommand) {
        validateSignupCommand(signupCommand);

        log.info("[UserService] 유저 회원가입");

        long startTime = System.currentTimeMillis();

        // 1. 아이디 중복 확인
        if (isDuplicateUserId(signupCommand.userId())) {
            throw new UserIdDuplicateException();
        }
        long afterDuplicateCheck = System.currentTimeMillis();
        log.info("[Timing] 아이디 중복 확인 소요 시간: {} ms", afterDuplicateCheck - startTime);

        // 2. 새로운 유저 생성 및 비밀번호 인코딩
        User userToSave = createNewUser(signupCommand);
        String encodedPassword = passwordEncoder.encode(signupCommand.password());
        userToSave.signupMember(signupCommand, encodedPassword, signupCommand.language());

        long afterUserCreation = System.currentTimeMillis();
        log.info("[Timing] 유저 생성 및 비밀번호 인코딩 소요 시간: {} ms", afterUserCreation - afterDuplicateCheck);

        // 3. 유저 저장
        userRepository.save(userToSave);
        long afterUserSave = System.currentTimeMillis();
        log.info("[Timing] 유저 저장 소요 시간: {} ms", afterUserSave - afterUserCreation);

        // 4. 프로필 이미지 처리
        String imageUrl = S3_IMAGE.DEFAULT_URL;
        userToSave.setProfileImg(imageUrl);
        userRepository.save(userToSave); // 프로필 이미지 업데이트 후 다시 저장
        long afterProfileImage = System.currentTimeMillis();
        log.info("[Timing] 프로필 이미지 처리 및 저장 소요 시간: {} ms", afterProfileImage - afterUserSave);

        // 5. 비밀번호 히스토리 저장
        passwordHistoryRepository.save(PasswordHistory.builder()
                .user(userToSave)
                .password(encodedPassword)
                .build());
        long afterPasswordHistory = System.currentTimeMillis();
        log.info("[Timing] 비밀번호 히스토리 저장 소요 시간: {} ms", afterPasswordHistory - afterProfileImage);

        //유저 학습 테이블 생성
        EducationDay educationDay = EducationDay.builder()
                .user(userToSave)
                .wordIds(new ArrayList<>())
                .day(1)
                .build();

        educationDayRepository.save(educationDay);

        // 6. 토큰 생성 및 저장
        try {
            String accessToken = jwtProcessor.generateAccessToken(userToSave);
            String refreshToken = jwtProcessor.generateRefreshToken(userToSave);
            jwtProcessor.saveRefreshToken(accessToken, refreshToken);

            long afterTokenGeneration = System.currentTimeMillis();
            log.info("[Timing] 토큰 생성 및 저장 소요 시간: {} ms", afterTokenGeneration - afterPasswordHistory);

            long totalTime = afterTokenGeneration - startTime;
            log.info("[Timing] 회원가입 전체 소요 시간: {} ms", totalTime);

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

        String updatedNickname = getUpdatedField(command.nickname(), currentUser.getNickname());

        currentUser.updateProfile(updatedNickname);
        userRepository.save(currentUser);

        return UserDetailResponse.of(
                currentUser.getProfileImg(),
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

        User user = getCurrentLoggedInMember();
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

    public List<ProfileUnlockedResponse> getProfileUnlocked() {
        User user = getCurrentLoggedInMember();
        int count = educationStampRepository.countByUser(user);
        int newCount = count / 5 + 5;
        int maxImageNumber = 16; // 현재 이미지가 16번까지 있다고 가정합니다.
        List<ProfileUnlockedResponse> response = new ArrayList<>();

        for (int i = 1; i <= maxImageNumber; i++) {
            boolean isUnlocked = i <= newCount;
            String url = String.format("https://horong-service.s3.ap-northeast-2.amazonaws.com/profileImg/%s.png", i);

            response.add(new ProfileUnlockedResponse(i, isUnlocked, url));
        }

        return response;
    }

    @Transactional
    public UserProfileDetailResponse updateProfileImage(Integer profileImageNumber) {
        log.info("[UserService] 프로필 이미지 변경");
        User user = getCurrentLoggedInMember();
        user.setProfileImg(profileImageNumber.toString());
        userRepository.save(user);
        UserProfileDetailResponse response = UserProfileDetailResponse.of(
                generatePreSignedUrl(user.getProfileImg()),
                user.getNickname()
        );
        return response;
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

    private String generatePreSignedUrl(String imageUrl) {
        if (imageUrl == null) {
            return "";
        }
//        String imagePath = extractImagePath(imageUrl);
        return s3Util.getProfilePresignedUrlFromS3(imageUrl);
    }

//    private String extractImagePath(String imageUrl) {
//        return imageUrl.substring(imageUrl.indexOf("profileImg/"));
//    }

    private String getUpdatedField(String newValue, String currentValue) {
        return (newValue == null || newValue.isEmpty()) ? currentValue : newValue;
    }

    private User getCurrentLoggedInMember() {
        Long userId = SecurityUtil.getLoginMemberId()
                .orElseThrow(NotAuthenticatedException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);

        if (user.isDeleted()) {
            throw new NotAuthenticatedException();
        }
        return user;
    }

    @Transactional
    public void updateLanguage(Language language) {
        User user = getCurrentLoggedInMember();
        user.setLanguage(language);
        userRepository.save(user);
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
        // Redis에서 금칙어 목록 가져오기
        Set<String> forbiddenWords = redisTemplateslang.opsForSet().members(FORBIDDEN_WORDS_KEY);

        if (command.userId().length() < 2 || command.userId().length() > 16) {
            throw new UserIdNotValidException();
        }
        if (!command.userId().matches("^[a-zA-Z0-9]+$")) {
            throw new NotAllowedUseridException();
        }
        if (containsForbiddenWord(command.userId(), forbiddenWords)) {
            throw new ForbiddenWordContainedException();
        }
        if (command.password().length() < 8 || command.password().length() > 20) {
            throw new PasswordNotValidExeption();
        }
        if (!command.password().matches(".*[!@#$%^&*].*")) {
            throw new InvalidPasswordException();
        }
        if (command.nickname().length() < 2 || command.nickname().length() > 20) {
            throw new NicknameNotValidExeption();
        }
        if (!command.nickname().matches("^[a-zA-Z0-9가-힣一-亜\u4e00-\u9fa5]+$")) {
            throw new NotAllowedNicknameException();
        }
        if (containsForbiddenWord(command.nickname(), forbiddenWords)) {
            throw new ForbiddenWordContainedException();
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
        Set<String> forbiddenWords = redisTemplateslang.opsForSet().members(FORBIDDEN_WORDS_KEY);
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
            if (containsForbiddenWord(command.nickname(), forbiddenWords)) {
                throw new ForbiddenWordContainedException();
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

    // 금칙어가 포함되어 있는지 확인하는 메서드
    private boolean containsForbiddenWord(String text, Set<String> forbiddenWords) {
        if (forbiddenWords == null || forbiddenWords.isEmpty()) {
            return false;
        }
        for (String word : forbiddenWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
