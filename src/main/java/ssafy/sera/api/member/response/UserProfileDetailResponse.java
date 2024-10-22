package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.domain.member.common.Gender;

@Schema(description = "유저 프로필 응답 DTO")
public record UserProfileDetailResponse(
        @Schema(description = "프로필 이미지 경로", example = "https://my-bucket.s3.amazonaws.com/sample-image.jpg\n")
        String profilePreSignedUrl,

        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname
) {
    public static UserProfileDetailResponse of(String profileImageBase64, String nickname) {
        return new UserProfileDetailResponse(
                profileImageBase64,
                nickname
        );
    }
}
