package ssafy.horong.api.community.response;

import java.util.List;

public record GetOriginPostResponse(
    GetPostResponse post,
    List<String> images
) {
}
