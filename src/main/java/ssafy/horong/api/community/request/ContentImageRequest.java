package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContentImageRequest(
        @Schema(description = "이미지 경로", example = "이미지 경로입니다.")
        String imageUrl
) {}
