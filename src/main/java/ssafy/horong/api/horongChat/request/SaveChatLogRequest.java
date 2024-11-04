package ssafy.horong.api.horongChat.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.horongChat.command.SaveChatLogCommand;

import java.util.List;

public record SaveChatLogRequest(
        @Schema(
                description = "채팅 리스트입니다",
                example = "[{\"content\": \"채팅 내용입니다.\", \"authorType\": \"USER\"}, {\"content\": \"다른 채팅 내용입니다.\", \"authorType\": \"BOT\"}]"
        )
        List<HorongChatContentRequest> chatContents
) {
    public SaveChatLogCommand toCommand() {
        return new SaveChatLogCommand(chatContents);
    }
}
