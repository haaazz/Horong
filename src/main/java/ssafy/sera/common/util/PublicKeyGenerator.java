package ssafy.sera.common.util;

import ssafy.sera.common.exception.other.PublicKeyGenerationException;
import ssafy.sera.domain.auth.model.PublicKey;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public class PublicKeyGenerator {

    public static RSAPublicKey execute(PublicKey key) {
        BigInteger modulus = new BigInteger(1, java.util.Base64.getUrlDecoder().decode(key.n()));
        BigInteger exponent = new BigInteger(1, java.util.Base64.getUrlDecoder().decode(key.e()));

        try {
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance(key.kty());
            java.security.spec.RSAPublicKeySpec keySpec = new java.security.spec.RSAPublicKeySpec(modulus, exponent);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (java.security.NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException exception) {
            throw new PublicKeyGenerationException();
        }
    }

}
