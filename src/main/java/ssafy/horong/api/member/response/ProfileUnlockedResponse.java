package ssafy.horong.api.member.response;

import java.net.URL;

public record ProfileUnlockedResponse(
        Integer ImageNumber,
        boolean isUnlocked,
        String imageUrl
) {

}
