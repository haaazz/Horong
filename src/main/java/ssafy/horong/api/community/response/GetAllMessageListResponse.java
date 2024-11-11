package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

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

        @Schema(description = "전송자 프로필 이미지", example = "https://horong.s3.ap-northeast-2.amazonaws.com/profile/1.jpg")
        URI profileImage,

        @Schema(description = "마지막 전송 시간", example = "2021-07-01T00:00:00")
        String createdAt,

        @Schema(description = "게시글 id", example = "1")
        Long postId
) {
    public static GetAllMessageListResponse of(Long roomId ,Long messageCount, String content, String senderNickname, Long senderId, URI profileImage, String createdAt, Long postId) {
        return new GetAllMessageListResponse(roomId ,messageCount, content, senderNickname, senderId, profileImage, createdAt, postId);
    }
}
