package ssafy.sera.domain.community.command;

public record CreateCommentCommand(
        Long postId,
        String content
) {
}
