package ssafy.horong.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssafy.horong.common.exception.BaseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilterExceptionProcessor {

    private final ObjectMapper objectMapper;

    public void excute(HttpServletResponse response, BaseException e) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("isSuccess", false);
        errorResponse.put("message", e.getErrorCode().getMessage());
        errorResponse.put("code", e.getErrorCode().getCode());
        errorResponse.put("httpStatus", e.getErrorCode().getHttpStatus());

        response.setCharacterEncoding("UTF-8");
        response.setStatus(e.getErrorCode().getHttpStatus());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
