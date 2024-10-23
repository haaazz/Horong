package ssafy.horong.common.exception.other;

public class PublicKeyGenerationException extends RuntimeException {
    public PublicKeyGenerationException() {
        super("Public Key Generation Failed");
    }
}
