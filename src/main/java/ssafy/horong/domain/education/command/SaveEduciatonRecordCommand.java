package ssafy.horong.domain.education.command;

import org.springframework.web.multipart.MultipartFile;

public record SaveEduciatonRecordCommand(
        String word,
        MultipartFile audio
) {

}
