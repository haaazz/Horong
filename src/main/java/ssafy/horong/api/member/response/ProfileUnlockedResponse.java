package ssafy.horong.api.member.response;

public record ProfileUnlockedResponse(
        Integer ImageNumber,
        boolean isUnlocked,
        String imageUrl
) {

}
