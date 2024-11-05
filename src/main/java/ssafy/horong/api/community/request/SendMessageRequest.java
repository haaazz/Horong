package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.community.command.SendMessageCommand;

import java.util.List;

@Schema(description = "메시지 전송 요청 DTO")
public record SendMessageRequest(
        @Schema(description = "받는 사람 닉네임", example = "test1")
        String receiverNickname,

        @Schema(description = "메시지 내용", example = "[{\"content\": \"이것은 한국어로 된 첫 번째 내용입니다.\", \"isOriginal\": true, \"language\": \"KOREAN\", \"contentImageRequest\": [{\"imageUrl\": \"http://example.com/image1.jpg\"}]}, {\"content\": \"This is the second content in English.\", \"isOriginal\": false, \"language\": \"ENGLISH\", \"contentImageRequest\": [{\"imageUrl\": \"http://example.com/image2.jpg\"}]}, {\"content\": \"这是第三个内容用中文写的。\", \"isOriginal\": true, \"language\": \"CHINESE\", \"contentImageRequest\": [{\"imageUrl\": \"http://example.com/image3.jpg\"}]}, {\"content\": \"これは日本語で書かれた四番目の内容です。\", \"isOriginal\": false, \"language\": \"JAPANESE\", \"contentImageRequest\": [{\"imageUrl\": \"http://example.com/image4.jpg\"}]}]")
        List<CreateContentByLanguageRequest> contentsByLanguages,

        @Schema(description = "이미지 리트스", example = "[{\"imageUrl\": \"https://horong-service.s3.ap-northeast-2.amazonaws.com/community/d0a0345b-a10e-4021-afd2-c81f29d0f070.png\"}, {\"imageUrl\": \"https://horong-service.s3.ap-northeast-2.amazonaws.com/community/d0a0345b-a10e-4021-afd2-c81f29d0f070.png\"}]")
        List<ContentImageRequest> contentImageRequest
) {
    public SendMessageCommand toCommand() {
        return new SendMessageCommand(receiverNickname, contentsByLanguages, contentImageRequest);
    }
}
