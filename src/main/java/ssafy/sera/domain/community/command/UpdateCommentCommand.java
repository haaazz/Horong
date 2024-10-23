package ssafy.sera.domain.community.command;

public record UpdateCommentCommand(
        Long commentId,
        String content
) {
}
