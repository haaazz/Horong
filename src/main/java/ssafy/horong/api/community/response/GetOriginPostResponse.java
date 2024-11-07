package ssafy.horong.api.community.response;

import ssafy.horong.domain.community.entity.ContentImage;

import java.util.List;

public record GetOriginPostResponse(
    GetPostResponse post,
    List<String> images
) {
}
