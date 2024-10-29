package ssafy.horong.api.health;

import org.springframework.web.multipart.MultipartFile;

public record mp3TestRequest (
        MultipartFile mp3
){
}
