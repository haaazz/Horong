package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetAllMessageListResponse(
        @Schema(description = "방id", example = "1")
        Long roomId,

        @Schema(description = "메시지 수", example = "1")
        Long messageCount,

        @Schema(description = "마지막 메시지 내용", example = "안녕하세요")
        String content,

        @Schema(description = "전송자 닉네임", example = "홍길동")
        String senderNickname,

        @Schema(description = "전송자 id", example = "1")
        Long senderId,

        @Schema(description = "마지막 전송 시간", example = "2021-07-01T00:00:00")
        String createdAt,

        @Schema(description = "게시글 id", example = "1")
        Long postId
) {
    public static GetAllMessageListResponse of(Long roomId ,Long messageCount, String content, String senderNickname, Long senderId, String createdAt, Long postId) {
        return new GetAllMessageListResponse(roomId ,messageCount, content, senderNickname, senderId, createdAt, postId);
    }
}
