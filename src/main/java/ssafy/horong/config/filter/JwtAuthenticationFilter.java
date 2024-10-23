package ssafy.horong.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ssafy.horong.domain.auth.model.DecodedJwtToken;
import ssafy.horong.common.util.JwtProcessor;
import ssafy.horong.common.exception.security.TokenExpiredException;
import ssafy.horong.common.exception.security.InvalidSignatureTokenException;
import ssafy.horong.common.exception.security.InvalidTokenException;
import ssafy.horong.common.exception.token.TokenTypeNotMatchedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProcessor jwtProcessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (Objects.nonNull(token)) {
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenExpiredException e) {
            log.error("TokenExpiredException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired: " + e.getMessage());
            return;
        } catch (InvalidSignatureTokenException e) {
            log.error("InvalidSignatureTokenException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature: " + e.getMessage());
            return;
        } catch (InvalidTokenException e) {
            log.error("InvalidTokenException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
            return;
        } catch (TokenTypeNotMatchedException e) {
            log.error("TokenTypeNotMatchedException: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token type mismatch: " + e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Unhandled exception: {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        DecodedJwtToken decodedJwtToken = jwtProcessor.decodeToken(token, ACCESS_TOKEN);

        Long memberId = decodedJwtToken.memberId();
        String role = decodedJwtToken.role();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (bearerToken != null && !bearerToken.isBlank()) {
            return bearerToken;
        }
        return null;
    }

    private void setErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write("{ \"error\": \"" + statusCode + "\", \"message\": \"" + message + "\" }");
    }
}
