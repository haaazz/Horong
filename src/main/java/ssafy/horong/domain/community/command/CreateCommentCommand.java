package ssafy.horong.domain.community.command;

public record CreateCommentCommand(
        Long postId,
        String content
) {
}
