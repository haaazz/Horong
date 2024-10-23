package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.command.CreatePostCommand;

@Schema(description = "게시글 생성 요청 DTO")
public record CreatePostRequest(
        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        @Size(max = 20, message = "제목은 20자 이하로 입력해야 합니다.")
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
        @Size(max = 255, message = "내용은 255자 이하로 입력해야 합니다.")
        String content,

        @Schema(description = "프로필 이미지 파일 목록 (최대 5개)", example = "profile1.jpg, profile2.jpg")
        MultipartFile[] images,

        @Schema(description = "게시판 타입", example = "free")
        String boardType
) {
        public CreatePostCommand toCommand() {
                return new CreatePostCommand(title, content, images, boardType);
        }
}
