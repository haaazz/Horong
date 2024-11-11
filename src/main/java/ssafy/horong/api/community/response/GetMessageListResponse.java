package ssafy.horong.api.community.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.entity.Message;

import java.net.URI;

public record GetMessageListResponse(
        @Schema(description = "메시지 내용", example = "안녕하세요")
        String content,

        @Schema(description = "전송자 닉네임", example = "홍길동")
        String senderNickname,

        @Schema(description = "전송자 id", example = "1")
        Long senderId,

        @Schema(description = "전송자 프로필 이미지", example = "https://horong.s3.ap-northeast-2.amazonaws.com/profile/1.jpg")
        URI profileImage,

        @Schema(description = "전송 시간", example = "2021-07-01T00:00:00")
        String createdAt,

        @Schema(description = "사용자여부", example = "USER")
        Message.UserMessageType userMessageType
) {
    public GetMessageListResponse of(String content, String senderNickname, Long senderId, URI profileImage ,String createdAt, Message.UserMessageType userMessageType) {
        return new GetMessageListResponse(content, senderNickname, senderId, profileImage, createdAt, userMessageType);
    }
}
