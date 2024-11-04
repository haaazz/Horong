// SaveChatLogCommand.java
package ssafy.horong.domain.horongChat.command;

import ssafy.horong.api.horongChat.request.HorongChatContentRequest;

import java.util.List;

public record SaveChatLogCommand(
        List<HorongChatContentRequest> chatContents
) {
}
