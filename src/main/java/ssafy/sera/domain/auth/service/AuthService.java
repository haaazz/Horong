package ssafy.sera.domain.auth.service;

import ssafy.sera.api.auth.request.TokenRefreshRequest;
import ssafy.sera.api.auth.response.AuthResponse;
import ssafy.sera.domain.auth.command.LoginCommand;

public interface AuthService {
    AuthResponse login(LoginCommand command);
    AuthResponse refresh(TokenRefreshRequest refreshToken);
    void logout();
}
