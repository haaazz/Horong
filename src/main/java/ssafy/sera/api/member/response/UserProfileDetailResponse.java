package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.sera.domain.member.common.Gender;

@Schema(description = "선수 프로필 응답 DTO")
public record UserProfileDetailResponse(
        @Schema(description = "프로필 이미지 경로", example = "https://my-bucket.s3.amazonaws.com/sample-image.jpg\n")
        String profilePreSignedUrl,

        @Schema(description = "닉네임", example = "쿠잉비")
        String nickname,

        @Schema(description = "성별", example = "MALE")
        String gender,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "이메일", example = "example@gmail.com")
        String email,

        @Schema(description = "한 줄 소개", example = "안녕하세요")
        String description
) {
    public static UserProfileDetailResponse of(String profileImageBase64, String nickname, Gender gender, String phoneNumber, String email, String description) {
        return new UserProfileDetailResponse(
                profileImageBase64,
                nickname,
                gender != null ? gender.getValue() : null,
                phoneNumber,
                email,
                description
        );
    }
}
