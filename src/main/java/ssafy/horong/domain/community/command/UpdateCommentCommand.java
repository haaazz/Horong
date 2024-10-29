package ssafy.horong.domain.community.command;

import ssafy.horong.api.community.request.CreateContentByLanguageRequest;

import java.util.List;

public record UpdateCommentCommand(
        Long commentId, // 댓글 ID
        List<CreateContentByLanguageRequest> contentByCountries // 언어별 콘텐츠 리스트
) {
}
