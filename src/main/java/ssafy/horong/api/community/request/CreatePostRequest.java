package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.command.CreatePostCommand;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "게시글 생성 요청 DTO")
public record CreatePostRequest(
        @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
        @Size(max = 20, message = "제목은 20자 이하로 입력해야 합니다.")
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
        @Size(max = 255, message = "내용은 255자 이하로 입력해야 합니다.")
        String content,

        @Schema(description = "첫 번째 이미지 파일", example = "profile1.jpg")
        MultipartFile image1,

        @Schema(description = "두 번째 이미지 파일", example = "profile2.jpg")
        MultipartFile image2,

        @Schema(description = "세 번째 이미지 파일", example = "profile3.jpg")
        MultipartFile image3,

        @Schema(description = "네 번째 이미지 파일", example = "profile4.jpg")
        MultipartFile image4,

        @Schema(description = "다섯 번째 이미지 파일", example = "profile5.jpg")
        MultipartFile image5,

        @Schema(description = "게시판 타입", example = "free")
        String boardType
) {
        public CreatePostCommand toCommand() {
                // null이 아닌 파일만 리스트에 추가
                List<MultipartFile> images = new ArrayList<>();
                if (image1 != null) images.add(image1);
                if (image2 != null) images.add(image2);
                if (image3 != null) images.add(image3);
                if (image4 != null) images.add(image4);
                if (image5 != null) images.add(image5);

                // 이미지 리스트를 배열로 변환하여 CreatePostCommand로 전달
                return new CreatePostCommand(title, content, images.toArray(new MultipartFile[0]), boardType);
        }
}
