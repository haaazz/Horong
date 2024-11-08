package ssafy.horong.api.community.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ssafy.horong.domain.community.command.GetMessageListCommand;

@Schema(description = "메시지 리스트 요청")
public record GetMessageListRequest(
        @Schema(description = "방 ID", example = "1")
        Long roomId
) {
    public GetMessageListCommand toCommand() {
        return new GetMessageListCommand(roomId);
    }
}
