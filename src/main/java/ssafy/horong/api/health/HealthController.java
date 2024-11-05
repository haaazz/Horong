package ssafy.horong.api.health;

import java.io.BufferedReader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ssafy.horong.api.CommonResponse;
import ssafy.horong.common.exception.data.DataNotFoundException;
import ssafy.horong.common.exception.errorcode.GlobalErrorCode;
import ssafy.horong.common.properties.WebClientProperties;
import ssafy.horong.common.util.S3Util;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Tag(name = "Health", description = "서버 상태 확인")
public class HealthController {

    private final RedisTemplate<String, ?> redisTemplate;
    private final DataSource dataSource;
    private final S3Util s3Util;
    private final WebClient webClient;
    private final WebClientProperties webClientProperties;
    private final RedisTemplate<String, String> redisTemplateslang;

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
        String imageUrl= s3Util.uploadToS3(request.image(), "test", "test/");
        return CommonResponse.ok(s3Util.getS3UrlFromS3(imageUrl));
    }

    @PostMapping(value = "/audio", consumes = { "multipart/form-data" })
    public CommonResponse<URI> uploadAudio(@ModelAttribute @Validated mp3TestRequest request) {

        MultipartFile audioFile = request.mp3(); // 파일을 가져옴
        log.info("Received file: {}", audioFile.getOriginalFilename());

        // mp3 파일을 S3에 업로드
        String audioUrl = s3Util.uploadToS3(audioFile, "audio", "testAudio/");

        // 업로드된 파일의 URL 반환
        return CommonResponse.ok(s3Util.getS3UrlFromS3(audioUrl));
    }

    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인합니다.")
    @GetMapping("/ping")
    public CommonResponse<String> ping() {
        log.info("[HealthController] 백엔드 서버 상태 확인");
        return CommonResponse.ok("pong");
    }

    @Operation(summary = "데이터 서버와 연결 확인", description = "데이터 서버와의 연결 상태를 확인합니다.")
    @GetMapping("/data-server/check")
    public CommonResponse<String> checkDataServerConnection() {
        log.info("[HealthController] 데이터 서버 연결 확인");

        String requestUrl = webClientProperties.url() + "name" + "/";

        String response = webClient.get()
                .uri(requestUrl)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new DataNotFoundException()))
                )
                .bodyToMono(String.class) // 간단한 String 응답으로 변경
//                .bodyToMono(PlayerMatchAnalyticsResponse.class) // 이렇게 response에 담아도 됨
                .blockOptional()
                .orElseThrow(DataNotFoundException::new);

        return CommonResponse.ok("데이터 서버 연결 성공: " + response, null);
    }

    private static final String FORBIDDEN_WORDS_KEY = "forbiddenWords";

    @Operation(summary = "금칙어 CSV 파일 업로드", description = "CSV 파일에서 금칙어를 읽어 Redis에 저장합니다.")
    @PostMapping("/upload/csv")
    public ResponseEntity<CommonResponse<String>> uploadForbiddenWordsFromCSV() {
        log.info("[HealthController] 금칙어 CSV 파일 업로드");

        Resource resource = new ClassPathResource("slang.csv");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String forbiddenWord = line.trim();
                if (!forbiddenWord.isEmpty()) {
                    // 이미 존재하는지 확인 후 추가
                    if (!redisTemplateslang.opsForSet().isMember(FORBIDDEN_WORDS_KEY, forbiddenWord)) {
                        redisTemplateslang.opsForSet().add(FORBIDDEN_WORDS_KEY, forbiddenWord); // 금칙어 추가
                        log.info("추가된 금칙어: {}", forbiddenWord);
                    } else {
                        log.info("이미 존재하는 금칙어: {}", forbiddenWord);
                    }
                }
            }
            return ResponseEntity.ok(CommonResponse.ok("금칙어가 성공적으로 추가되었습니다.", null));
        } catch (Exception e) {
            log.error("금칙어 추가 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.internalServerError(GlobalErrorCode.SERVER_ERROR));
        }
    }

    @Operation(summary = "금칙어확인", description = "Redis에 저장된 금칙어 목록을 확인합니다.")
    @GetMapping("/check/slang")
    public ResponseEntity<CommonResponse<List<String>>> checkForbiddenWords() {
        log.info("[HealthController] 금칙어 확인");

        try {
            // Redis에서 금칙어 가져오기
            Set<String> forbiddenWords = redisTemplateslang.opsForSet().members(FORBIDDEN_WORDS_KEY);

            if (forbiddenWords != null && !forbiddenWords.isEmpty()) {
                log.info("Redis에 저장된 금칙어 목록:");
                for (String word : forbiddenWords) {
                    log.info("금칙어: {}", word);
                }
                return ResponseEntity.ok(CommonResponse.ok("금칙어 목록을 성공적으로 가져왔습니다.", new ArrayList<>(forbiddenWords)));
            } else {
                log.info("Redis에 금칙어가 없습니다.");
                return ResponseEntity.ok(CommonResponse.ok("금칙어 목록이 비어 있습니다.", null));
            }
        } catch (Exception e) {
            log.error("금칙어 확인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.internalServerError(GlobalErrorCode.SERVER_ERROR));
        }
    }
}
