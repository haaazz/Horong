package ssafy.horong.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssafy.horong.common.exception.security.IssuerTokenIncorrectException;
import ssafy.horong.common.exception.security.TokenExpiredException;
import ssafy.horong.common.exception.security.InvalidSignatureTokenException;
import ssafy.horong.common.exception.security.InvalidTokenException;
import ssafy.horong.domain.auth.model.PublicKey;
import ssafy.horong.domain.auth.model.UserInfo;
import ssafy.horong.domain.auth.command.KakaoLoginCommand;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final ObjectMapper objectMapper;

    public String getKid(String idToken) {
        try {
            String[] tokenSplit = idToken.split("\\.");
            String header = tokenSplit[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(header), StandardCharsets.UTF_8);
            return objectMapper.readValue(decodedHeader, Map.class).get("kid").toString();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public UserInfo getUserInfo(String idToken, RSAPublicKey key, String iss, String aud) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(iss)
                    .requireAudience(aud)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();
            return new UserInfo(claims.get("nickname", String.class), claims.get("email", String.class), claims.get("profile_image", String.class));
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (IncorrectClaimException e) {
            throw new IssuerTokenIncorrectException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public UserInfo getUserInfo(KakaoLoginCommand kakaoLoginCommand) {
        PublicKey key = kakaoLoginCommand.publicKeys().keys().stream()
                .filter(k -> k.kid().equals(kakaoLoginCommand.kid()))
                .findFirst()
                .orElseThrow(IssuerTokenIncorrectException::new);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(PublicKeyGenerator.execute(key))
                    .requireIssuer(kakaoLoginCommand.iss())
                    .requireAudience(kakaoLoginCommand.aud())
                    .build()
                    .parseSignedClaims(kakaoLoginCommand.idToken())
                    .getPayload();
            return new UserInfo(claims.get("nickname", String.class), claims.get("email", String.class), claims.get("profile_image", String.class));
        } catch (SignatureException e) {
            throw new InvalidSignatureTokenException();
        } catch (IncorrectClaimException e) {
            throw new IssuerTokenIncorrectException();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

}
