package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 ID 응답 DTO")
public record UserIdResponse(
        @Schema(description = "회원 ID", example = "1")
        Long userId
) {
    public static UserIdResponse of(Long userId) {
        return new UserIdResponse(userId);
    }
}
