package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "국가별 콘텐츠 응답 DTO")
public record ContentByCountryResponse(
        @Schema(description = "내용", example = "내용입니다.")
        String content,

        @Schema(description = "원본 여부", example = "true")
        boolean isOriginal,

        @Schema(description = "국가", example = "KOREA")
        String country,

        @Schema(description = "콘텐츠 이미지 리스트", example = "[\"https://my-bucket.s3.amazonaws.com/image1.jpg\", \"https://my-bucket.s3.amazonaws.com/image2.jpg\"]")
        List<String> imageList
) {}
