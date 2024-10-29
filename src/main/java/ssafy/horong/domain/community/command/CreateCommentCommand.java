package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record CreateCommentCommand(
        Long postId,
        String content,
        List<CreateContentByLanguageRequest>contentByCountries
) {
}
