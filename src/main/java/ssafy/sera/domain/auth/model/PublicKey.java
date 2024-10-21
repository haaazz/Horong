package ssafy.sera.domain.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PublicKey (
    String kid, // public key id
    String kty, // public key type : RSA로 고정
    String alg, // algorithm : 암호화 알고리즘
    String use, // public key use : 공개키 용도; sig(서명)으로 고정
    String n, // modulus : 공개키 모듈
    String e // exponent : 공개키 지수
) { }
