package ssafy.sera.common.exception.other;

public class PublicKeyGenerationException extends RuntimeException {
    public PublicKeyGenerationException() {
        super("Public Key Generation Failed");
    }
}
