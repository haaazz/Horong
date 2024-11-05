package ssafy.horong.api.education.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;

public record SaveEduciatonRecordRequest(
        @Schema(description = "단어", example = "안녕하세요")
        String word,

        @Schema(description = "발음 오디오", example = "hello.mp3")
        MultipartFile audio
) {
    public SaveEduciatonRecordCommand toCommand() {
        return new SaveEduciatonRecordCommand(word, audio);
    }
}
