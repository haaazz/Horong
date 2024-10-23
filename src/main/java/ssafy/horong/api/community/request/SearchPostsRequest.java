package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.SearchPostsCommand;

@Schema(description = "게시글 검색 요청 DTO")
public record SearchPostsRequest(
        @Schema(description = "검색할 키워드", example = "키워드")
        String keyword
) {
        public SearchPostsCommand toCommand() {
                return new SearchPostsCommand(keyword);
        }
}
