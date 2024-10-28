package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 상세 응답 DTO")
public record GetPostResponse(
        @Schema(description = "게시글 id", example = "1")
        Long postId,

        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        String title,

        @Schema(description = "게시글 작성자 닉네임", example = "호롱")
        String nickname,

        @Schema(description = "게시글 국가별 콘텐츠 리스트")
        String contents,

        @Schema(description = "댓글 리스트")
        List<GetCommentResponse> comments
) {}
