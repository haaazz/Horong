package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.domain.member.common.Gender;
import ssafy.sera.domain.member.entity.User;


@Schema(description = "유저 정보 요약 응답 DTO")
public record UserSimpleResponse(
        @Schema(description = "유저 ID")
        Long id,

        @Schema(description = "유저 별명")
        String nickname,

        @Schema(description = "유저 성별")
        Gender gender,

        @Schema(description = "유저 프로필 이미지 presigned url")
        String image
) {
    public static UserSimpleResponse from(User user, String image) {
        if (user == null) {
            return null;
        }
        return new UserSimpleResponse(
                user.getId(),
                user.getNickname(),
                user.getGender(),
                image
        );
    }
}
