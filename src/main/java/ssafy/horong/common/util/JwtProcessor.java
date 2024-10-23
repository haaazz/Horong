package ssafy.horong.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ssafy.horong.common.exception.security.TokenExpiredException;
import ssafy.horong.common.exception.security.InvalidSignatureTokenException;
import ssafy.horong.common.exception.security.InvalidTokenException;
import ssafy.horong.common.exception.token.TokenTypeNotMatchedException;
import ssafy.horong.common.properties.JwtProperties;
import ssafy.horong.domain.auth.model.DecodedJwtToken;
import ssafy.horong.domain.auth.model.LoginToken;
import ssafy.horong.domain.member.common.MemberRole;
import ssafy.horong.domain.member.entity.User;
import ssafy.horong.domain.redis.BlacklistTokenRedisRepository;
import ssafy.horong.domain.redis.RefreshTokenRedisRepository;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import static ssafy.horong.common.constant.redis.KEY_PREFIX.ACCESS_TOKEN;
import static ssafy.horong.common.constant.redis.KEY_PREFIX.REFRESH_TOKEN;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtProcessor {

    private final JwtProperties jwtProperties;
    private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes());
    }

    public Jws<Claims> getClaim(String token) {
        log.debug("token : {}", token);
        if (isTokenExpired(token)) {
            throw new TokenExpiredException();
        }
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSecretKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public void saveRefreshToken(String accessToken, String refreshToken) {
        refreshTokenRedisRepository.save(accessToken, refreshToken);
    }

    public void saveRefreshToken(LoginToken tokens) {
        refreshTokenRedisRepository.save(tokens.accessToken(), tokens.refreshToken());
    }

    public String findRefreshTokenById(String accessToken) {
        return refreshTokenRedisRepository.findById(accessToken)
                .orElseThrow(InvalidTokenException::new);
    }

    public void renewRefreshToken(String oldRefreshToken, String newRefreshToken, User member) {
        refreshTokenRedisRepository.save(newRefreshToken, String.valueOf(member.getId()));
        expireToken(oldRefreshToken);
    }

    public void expireToken(String accessToken) {
        String refreshToken = getRefreshToken(accessToken);
        if (refreshToken == null) {
            log.info("리프레시 토큰을 찾지 못해 토큰 만료 처리 건너뜀: {}", accessToken);
            return;
        }
        blacklistTokenRedisRepository.save(refreshToken, getRemainingTime(refreshToken));
        refreshTokenRedisRepository.delete(refreshToken);
        log.info("Token added to blacklist: {}", refreshToken);
    }

    public long getRemainingTime(String token) {
        Claims claims = getClaim(token).getPayload();
        Date expiration = claims.getExpiration();
        Date now = new Date();
        return Math.max(0, expiration.getTime() - now.getTime());
    }

    public boolean isTokenExpired(String token) {
        Boolean result = blacklistTokenRedisRepository.hasKey(token);
        if (result != null) {
            return result;
        }
        return getClaim(token).getPayload().getExpiration().before(new Date());
    }

    public String generateAccessToken(User user) {
        log.debug("access token exp : {}", jwtProperties.accessTokenExp());
        return issueToken(user.getId(), user.getRole(), ACCESS_TOKEN, jwtProperties.accessTokenExp());
    }

    public String generateRefreshToken(User member) {
        return issueToken(member.getId(), member.getRole(), REFRESH_TOKEN, jwtProperties.refreshTokenExp());
    }

    public DecodedJwtToken decodeToken(String token, String type) {
        Claims claims = getClaim(token).getPayload();
        checkType(claims, type);

        return new DecodedJwtToken(
                Long.valueOf(claims.getSubject()),
                String.valueOf(claims.get("role")),
                String.valueOf(claims.get("type"))
        );
    }

    private String issueToken(Long userId, MemberRole role, String type, Long time) {
        Date now = new Date();
        return Jwts.builder()
                .issuer("Cooing Inc.")
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + time))
                .claim("type", type)
                .claim("role", role.name())
                .signWith(getSecretKey())
                .compact();
    }

    private void checkType(Claims claims, String type) {
        if (!type.equals(String.valueOf(claims.get("type")))) {
            throw new TokenTypeNotMatchedException();
        }
    }

    private String getRefreshToken(String accessToken) {
        return refreshTokenRedisRepository.findById(accessToken).orElse(null);
    }

}
