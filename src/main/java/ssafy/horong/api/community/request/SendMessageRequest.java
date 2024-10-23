package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.command.SendMessageCommand;

@Schema(description = "메시지 전송 요청 DTO")
public record SendMessageRequest(
        @Schema(description = "받는 사람 닉네임", example = "test1")
        String receiverNickname,

        @Schema(description = "메시지 내용", example = "안녕하세요.")
        String content,

        @Schema(description = "메시지 이미지", example = "https://my-bucket.s3.amazonaws.com/sample-image.jpg\n")
        MultipartFile image
) {
    public SendMessageCommand toCommand() {
        return new SendMessageCommand(receiverNickname, content, image);
    }
}
