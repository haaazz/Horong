package ssafy.horong.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import ssafy.horong.common.constant.global.S3_IMAGE;
import ssafy.horong.common.exception.s3.ExtensionNotAllowedException;
import ssafy.horong.common.exception.s3.PresignedUrlGenerationFailException;
import ssafy.horong.common.exception.s3.S3UploadFailedException;
import ssafy.horong.common.properties.S3Properties;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".PNG", ".JPG", ".JPEG", ".GIF", ".mp3", ".MP3", ".wav", ".WAV");

    private final AmazonS3 amazonS3Client;
    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;

    private static void validateFileExtension(String extension) {
        log.info("확장자: {}", extension);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ExtensionNotAllowedException();
        }
    }

    private static String getS3FileName(MultipartFile image, String text, String location) {
        String originalFilename = image.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        validateFileExtension(extension);

        return location + text + extension;
    }

    public String uploadImageToS3(MultipartFile imageFile, String text, String location) {
        try {
            String fileName = getS3FileName(imageFile, text, location);
            amazonS3Client.putObject(new PutObjectRequest(s3Properties.s3().bucket(), fileName, imageFile.getInputStream(), null));
            return fileName; // 객체 키만 반환
        } catch (IOException e) {
            throw new S3UploadFailedException();
        }
    }

    public String uploadUserImageToS3(MultipartFile imageFile, Long userId, String location, String existingImageUrl) {
        if (imageFile == null || imageFile.isEmpty()) {
            return existingImageUrl != null && !existingImageUrl.isEmpty() ? existingImageUrl : S3_IMAGE.DEFAULT_URL;
        }

        try {
            String fileName = getS3FileName(imageFile, userId.toString(), location);
            amazonS3Client.putObject(new PutObjectRequest(s3Properties.s3().bucket(), fileName, imageFile.getInputStream(), null));
            return fileName; // 객체 키만 반환
        } catch (IOException e) {
            throw new S3UploadFailedException();
        }
    }

    public List<String> uploardBoardImageToS3(MultipartFile[] images, Long postId) {
        int count = 0;
        String location = "Board/";
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            validateFileExtension(extension);
            String fileName = location + count+ "of" + postId + extension;
            try {
                amazonS3Client.putObject(new PutObjectRequest(s3Properties.s3().bucket(), fileName, image.getInputStream(), null));
                log.info("S3에 이미지 업로드 성공: {}", fileName);
                imageUrls.add(fileName); // 객체 키만 저장
            } catch (IOException e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage());
                throw new S3UploadFailedException();
            }
        }
        return imageUrls;
    }

    public String getPresignedUrlFromS3(String imagePath) {
        try {
            String objectKey = extractObjectKey(imagePath);

            GetObjectRequest getObjectRequest = createGetObjectRequest(objectKey);
            GetObjectPresignRequest getObjectPresignRequest = createGetObjectPresignRequest(getObjectRequest);

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            URL presignedUrl = presignedRequest.url();

            log.info("{} 이미지에 대한 presigned URL 생성 성공", objectKey);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 오류 발생: {}", e.getMessage());
            throw new PresignedUrlGenerationFailException();
        }
    }

    public URI getS3UrlFromS3(String imagePath) {
        // imagePath에서 S3 객체 키 추출
        String objectKey = extractObjectKey(imagePath);
        log.info(objectKey);

        // S3 URL 생성
        // S3에서 객체를 가져오기 위해 버킷 이름과 객체 키가 필요합니다.
        return amazonS3Client.getObject(s3Properties.s3().bucket(), objectKey).getObjectContent().getHttpRequest().getURI(); // 추출한 키를 사용해 URL 생성
    }

    private String extractObjectKey(String imagePath) {
        return imagePath.replace("https://sera-image.s3.ap-northeast-2.amazonaws.com/", "");
    }

    public String getFullS3ImageUrl(String objectKey) {
        // 객체 키에 S3 URL을 붙여서 반환
        return "https://sera-image.s3.ap-northeast-2.amazonaws.com/" + objectKey;
    }

    private GetObjectRequest createGetObjectRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(objectKey)
                .build();
    }

    private GetObjectPresignRequest createGetObjectPresignRequest(GetObjectRequest getObjectRequest) {
        return GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
    }
}
