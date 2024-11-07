package ssafy.horong.domain.member.service;

import ssafy.horong.api.member.response.*;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;
import ssafy.horong.domain.member.common.Language;

import java.util.List;

public interface UserService {
    UserSignupResponse signupMember(MemberSignupCommand signupCommand);
    UserProfileDetailResponse getMemberProfileDetail();
    UserDetailResponse getMemberDetail();
    UserDetailResponse updateMemberProfile(UpdateProfileCommand command);
    String deleteMember();
    boolean checkNickname(String nickname);
    boolean checkUserId(String userId);
    void updateMemberPassword(PasswordUpdateCommand command);
    UserIdResponse getMemberId();
    void updateLanguage(Language language);
    List<ProfileUnlockedResponse> getProfileUnlocked();
    UserProfileDetailResponse updateProfileImage(Integer profileImage);
}
