package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import ssafy.horong.domain.community.command.CreatePostCommand;
import ssafy.horong.domain.community.entity.BoardType;

import java.util.List;

@Schema(description = "게시글 생성 요청 DTO")
public record CreatePostRequest(
        @Schema(description = "게시글 제목 및 내용",
                example = "["
                        + "{\"title\": \"게시글 제목입니다.\", \"content\": \"이것은 한국어로 된 첫 번째 내용입니다.\", \"isOriginal\": true, \"language\": \"KOREAN\"}, "
                        + "{\"title\": \"Post Title in English\", \"content\": \"This is the second content in English.\", \"isOriginal\": false, \"language\": \"ENGLISH\"}, "
                        + "{\"title\": \"标题在中文\", \"content\": \"这是第三个内容用中文写的。\", \"isOriginal\": true, \"language\": \"CHINESE\"}, "
                        + "{\"title\": \"日本語のタイトル\", \"content\": \"これは日本語で書かれた四番目の内容です。\", \"isOriginal\": false, \"language\": \"JAPANESE\"} "
                        + "]")

        List<CreateContentByLanguageRequest> content,

        @Schema(description = "게시판 타입", example = "FREE")
        BoardType boardType,

        @Schema(description = "이미지 경로", example = "[{\"imageUrl\": \"http://example.com/image1.jpg\"}, {\"imageUrl\": \"http://example.com/image2.jpg\"}]")
        List<ContentImageRequest> contentImageRequest
) {
        public CreatePostCommand toCommand() {
                return new CreatePostCommand(content, boardType, contentImageRequest);
        }
}
