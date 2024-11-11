package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Schema(description = "게시글 상세 응답 DTO")
public record GetPostResponse(
        @Schema(description = "게시글 id", example = "1")
        Long postId,

        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        String title,

        @Schema(description = "게시글 작성자 닉네임", example = "호롱")
        String nickname,

        @Schema(description = "작성자 ID", example = "1")
        Long userId,

        @Schema(description = "게시글 국가별 콘텐츠 리스트")
        String contents,

        @Schema(description = "게시글 작성 일시", example = "2021-07-01 00:00:00")
        String createdAt,

        @Schema(description = "댓글 리스트")
        List<GetCommentResponse> comments,

        @Schema(description = "작성자 프로필 이미지", example = "https://horong.s3.ap-northeast-2.amazonaws.com/profile/1.jpg")
        String profileImage
) {}
