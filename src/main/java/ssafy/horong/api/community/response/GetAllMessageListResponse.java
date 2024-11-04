package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllMessageListResponse(
        @Schema(description = "메시지 수", example = "1")
        Long messageCount,

        @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
        String content,

        @Schema(description = "전송자 닉네임", example = "홍길동")
        String senderNickname
) {
    public GetAllMessageListResponse of(Long messageCount, String content, String senderNickname) {
        return new GetAllMessageListResponse(messageCount, content, senderNickname);
    }
}
