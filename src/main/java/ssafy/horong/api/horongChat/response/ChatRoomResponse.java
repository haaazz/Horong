package ssafy.horong.api.horongChat.response;

import java.util.List;

public record ChatRoomResponse(
        Long roomId,
        List<ChatContentResponse> chatContentList
) {
}
