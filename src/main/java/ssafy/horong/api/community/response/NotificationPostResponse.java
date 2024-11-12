package ssafy.horong.api.community.response;

public record NotificationPostResponse(
        String type,
        Long postId,
        String title

) {
}
