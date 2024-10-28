package ssafy.horong.api.health;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.errorcode.GlobalErrorCode;
import ssafy.horong.common.util.S3Util;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Tag(name = "Health", description = "서버 상태 확인")
public class HealthController {

    private final RedisTemplate<String, ?> redisTemplate;
    private final DataSource dataSource;
    private final S3Util s3Util;

    @Operation(summary = "Redis 연결 확인", description = "Redis 서버와의 연결 상태를 확인합니다.")
    @GetMapping("/redis/check")
    public CommonResponse<String> checkRedisConnection() {
        log.info("[HealthController] Redis 연결 확인");
        try {
            String pingResponse = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getClusterConnection().ping();
            if (!"PONG".equals(pingResponse)) {
                return CommonResponse.ok("Redis 연결 실패", null);
            }
            return CommonResponse.ok("Redis 연결 성공", null);
        } catch (Exception e) {
            log.error("Redis 연결 오류 발생", e);
            return CommonResponse.internalServerError(GlobalErrorCode.SERVER_ERROR);
        }
    }

    @Operation(summary = "mysql 연결 확인", description = "mysql 데이터베이스와의 연결 상태를 확인합니다.")
    @GetMapping("/mysql/check")
    public CommonResponse<String> checkmysqlConnection() {
        log.info("[HealthController] mysql 연결 확인");
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(2)) {  // 2초 내에 연결 확인
                return CommonResponse.ok("mysql 연결 실패", null);
            }
            return CommonResponse.ok("mysql 연결 성공", null);
        } catch (Exception e) {
            log.error("mysql 연결 오류 발생", e);
            return CommonResponse.internalServerError(GlobalErrorCode.SERVER_ERROR);
        }
    }

    @Operation(summary = "이미지 전송 확인", description = "이미지 전송이 정상적으로 동작하는지 확인합니다.")
    @PostMapping(value = "/image", consumes = { "multipart/form-data" })
    public CommonResponse<URI> checkImageTransfer(@ModelAttribute @Validated TestRequest request) {

        log.info("health{}", request.image());
        String imageUrl= s3Util.uploadImageToS3(request.image(), "test", "test");
        return CommonResponse.ok(s3Util.getS3UrlFromS3(imageUrl));
    }

    @PostMapping(value = "/audio", consumes = { "multipart/form-data" })
    public CommonResponse<URI> uploadAudio(@ModelAttribute @Validated mp3TestRequest request) {

        MultipartFile audioFile = request.mp3(); // 파일을 가져옴
        log.info("Received file: {}", audioFile.getOriginalFilename());

        // mp3 파일을 S3에 업로드
        String audioUrl = s3Util.uploadImageToS3(audioFile, "audio", "testAudio");

        // 업로드된 파일의 URL 반환
        return CommonResponse.ok(s3Util.getS3UrlFromS3(audioUrl));
    }



    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인합니다.")
    @GetMapping("/ping")
    public CommonResponse<String> ping() {
        log.info("[HealthController] 백엔드 서버 상태 확인");
        return CommonResponse.ok("pong");
    }

}
