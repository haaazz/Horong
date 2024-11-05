package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.ContentImageRequest;
import ssafy.horong.api.community.request.CreateContentByLanguageRequest;
import ssafy.horong.domain.community.entity.BoardType;

import java.util.List;

public record CreatePostCommand(
        List<CreateContentByLanguageRequest> content,
        BoardType boardType,
        List<ContentImageRequest> contentImageRequest
) {}
