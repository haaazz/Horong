package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.UpdateCommentCommand;

import java.util.List;

@Schema(description = "댓글 수정 요청 DTO")
public record UpdateCommentRequest(
        @Schema(description = "댓글 id", example = "1")
        Long commentId,

        @Schema(description = "언어별 콘텐츠 리스트.", example = "[{\"content\": \"이것은 한국어 댓글입니다.\", \"isOriginal\": true, \"language\": \"KOREAN\"}, {\"content\": \"This is an English comment.\", \"isOriginal\": false, \"language\": \"ENGLISH\"}, {\"content\": \"这是中文评论。\", \"isOriginal\": true, \"language\": \"CHINESE\"}, {\"content\": \"これは日本語のコメントです。\", \"isOriginal\": false, \"language\": \"JAPANESE\"}]")
        List<CreateContentByLanguageRequest> contentByCountries // 언어별 콘텐츠 리스트 추가
) {
        public UpdateCommentCommand toCommand() {
                return new UpdateCommentCommand(commentId, contentByCountries);
        }
}
