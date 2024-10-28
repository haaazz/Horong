package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.CreateCommentCommand;

import java.util.List;

@Schema(description = "댓글 생성 요청 DTO")
public record CreateCommentRequest(
        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
        String content,

        @Schema(description = "언어별 콘텐츠 리스트.", example = "[{\"content\": \"이것은 한국어 댓글입니다.\", \"isOriginal\": true, \"language\": \"KOREAN\"}, {\"content\": \"This is an English comment.\", \"isOriginal\": false, \"language\": \"ENGLISH\"}, {\"content\": \"这是中文评论。\", \"isOriginal\": true, \"language\": \"CHINESE\"}, {\"content\": \"これは日本語のコメントです。\", \"isOriginal\": false, \"language\": \"JAPANESE\"}]")
        List<CreateContentByLanguageRequest> contentByCountries
) {
    public CreateCommentCommand toCommand() {
        return new CreateCommentCommand(postId, content, contentByCountries);
    }
}
