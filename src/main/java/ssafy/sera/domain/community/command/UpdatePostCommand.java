package ssafy.sera.domain.community.command;

public record UpdatePostCommand(
        Long postId,
        String title,
        String content
) {
}
