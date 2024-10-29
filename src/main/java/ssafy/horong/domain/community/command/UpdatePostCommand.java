package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record UpdatePostCommand(
        Long postId,
        String title,
        List<CreateContentByLanguageRequest> content
) {}
