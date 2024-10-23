package ssafy.horong.api.member.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserTotalMatchResponse(
        @Schema(description = "플레이어 경기 수")
        Integer totalMatch
) {
    public static UserTotalMatchResponse of(Integer totalMatch) {
        return new UserTotalMatchResponse(totalMatch);
    }
}
