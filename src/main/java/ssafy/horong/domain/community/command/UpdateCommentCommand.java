package ssafy.horong.domain.community.command;

public record UpdateCommentCommand(
        Long commentId,
        String content
) {
}
