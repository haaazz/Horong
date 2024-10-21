package ssafy.sera.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "선수 ID 응답 DTO")
public record UserIdResponse(
        @Schema(description = "회원 ID", example = "1")
        Long playerId
) {
    public static UserIdResponse of(Long playerId) {
        return new UserIdResponse(playerId);
    }
}
