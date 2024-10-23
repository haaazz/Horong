package ssafy.horong.domain.member.service;

import ssafy.horong.api.member.response.UserDetailResponse;
import ssafy.horong.api.member.response.UserIdResponse;
import ssafy.horong.api.member.response.UserProfileDetailResponse;
import ssafy.horong.api.member.response.UserSignupResponse;
import ssafy.horong.domain.member.command.MemberSignupCommand;
import ssafy.horong.domain.member.command.PasswordUpdateCommand;
import ssafy.horong.domain.member.command.UpdateProfileCommand;

public interface UserService {
    UserSignupResponse signupMember(MemberSignupCommand signupCommand);
    UserProfileDetailResponse getMemberProfileDetail();
    UserDetailResponse getMemberDetail();
    UserDetailResponse updateMemberProfile(UpdateProfileCommand command);
    String deleteMember();
    boolean checkNickname(String nickname);
    void updateMemberPassword(PasswordUpdateCommand command);
    UserIdResponse getMemberId();

}
