package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 응답 DTO")
public record UserDetailResponse(
        @Schema(description = "프로필 이미지 경로", example = "https://my-bucket.s3.amazonaws.com/sample-image.jpg\n")
        String profilePreSignedUrl,

        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname
) {
    public static UserDetailResponse of(String profileImageBase64, String nickname) {
        return new UserDetailResponse(profileImageBase64, nickname);
    }
}
