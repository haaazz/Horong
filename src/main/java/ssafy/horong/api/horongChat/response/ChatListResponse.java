package ssafy.horong.api.horongChat.response;

import java.util.List;

public record ChatListResponse(
        List<ChatRoomResponse> chatList
) {
}
