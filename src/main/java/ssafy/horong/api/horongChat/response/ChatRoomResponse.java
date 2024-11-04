package ssafy.horong.api.horongChat.response;

import java.util.List;

public record ChatRoomResponse(
        Long RoomId,
        List<ChatContentResponse> chatContentList
) {
}
