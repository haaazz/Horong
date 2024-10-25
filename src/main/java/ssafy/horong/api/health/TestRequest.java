package ssafy.horong.api.health;

import org.springframework.web.multipart.MultipartFile;

public record TestRequest(
        MultipartFile image
) {
}
