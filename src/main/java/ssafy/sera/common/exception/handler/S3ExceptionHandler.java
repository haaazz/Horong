package ssafy.sera.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.sera.api.CommonResponse;
import ssafy.sera.common.exception.s3.*;

@RestControllerAdvice
@Slf4j
public class S3ExceptionHandler {

    @ExceptionHandler(ExtensionNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleNotAllowedExtension(ExtensionNotAllowedException e) {
        log.error("NotAllowedExtension Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(ImageTooLargeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleImageTooLargeException(ImageTooLargeException e) {
        log.error("ImageTooLargeException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(ProfileNotFoundInS3Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResponse handleProfileNotFoundInS3Exception(ProfileNotFoundInS3Exception e) {
        log.error("ProfileNotFoundInS3Exception Error", e);
        return CommonResponse.notFound(e.getErrorCode());
    }

    @ExceptionHandler(S3UploadFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handleImageUploadFailedException(S3UploadFailedException e) {
        log.error("S3UploadFailedException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }

    @ExceptionHandler(PresignedUrlGenerationFailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse handlePresignedUrlGenerationFailedException(PresignedUrlGenerationFailException e) {
        log.error("PresignedUrlGenerationFailException Error", e);
        return CommonResponse.badRequest(e.getErrorCode());
    }
}
