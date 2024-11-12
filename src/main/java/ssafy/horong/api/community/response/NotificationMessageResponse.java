package ssafy.horong.api.community.response;

public record NotificationMessageResponse(
        Long messageId,
        String message,
        String type,
        Long roomId
) {
}
