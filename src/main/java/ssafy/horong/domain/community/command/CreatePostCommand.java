package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.domain.community.entity.BoardType;

import java.util.List;

public record CreatePostCommand(
        String title,
        List<CreateContentByLanguageRequest> content,
        BoardType boardType
) {}
