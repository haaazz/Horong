package ssafy.horong.api.shortForm.response;

public record ShortFromListResponse(
        Long id,
        String content,
        String image,
        String audio,
        Integer is_saved,
        Boolean preference
) {
}
