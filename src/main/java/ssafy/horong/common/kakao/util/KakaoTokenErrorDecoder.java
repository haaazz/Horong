package ssafy.horong.common.kakao.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ssafy.horong.common.kakao.response.KakaoErrorResponse;
import ssafy.horong.common.exception.other.KakaoTokenException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class KakaoTokenErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.body() != null) {
            try {
                String message = Util.toString(response.body().asReader(Util.UTF_8));
                KakaoErrorResponse errorResponseForm =
                        objectMapper.readValue(message, KakaoErrorResponse.class);
                return new KakaoTokenException(
                        response.status(),
                        methodKey,
                        errorResponseForm.errorCode(),
                        errorResponseForm.errorDescription()
                );
            } catch (IOException e) {
                log.error("{}: Error Deserializing response body from failed feign request response.", methodKey, e);
            }
        }

        return new KakaoTokenException(response.status(), methodKey, "KAKAO_SERVER_ERROR", null);
    }
}
