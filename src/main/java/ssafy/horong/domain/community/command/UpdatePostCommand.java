package ssafy.horong.domain.community.command;

public record UpdatePostCommand(
        Long postId,
        String title,
        String content
) {
}
