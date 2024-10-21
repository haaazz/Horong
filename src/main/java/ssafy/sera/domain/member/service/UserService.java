package ssafy.sera.domain.member.service;

import ssafy.sera.api.member.response.UserDetailResponse;
import ssafy.sera.api.member.response.UserIdResponse;
import ssafy.sera.api.member.response.UserProfileDetailResponse;
import ssafy.sera.api.member.response.UserSignupResponse;
import ssafy.sera.domain.member.command.MemberSignupCommand;
import ssafy.sera.domain.member.command.PasswordUpdateCommand;
import ssafy.sera.domain.member.command.UpdateProfileCommand;

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
