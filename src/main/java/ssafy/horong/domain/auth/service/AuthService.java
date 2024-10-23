package ssafy.horong.domain.auth.service;

import ssafy.horong.api.auth.request.TokenRefreshRequest;
import ssafy.horong.api.auth.response.AuthResponse;
import ssafy.horong.domain.auth.command.LoginCommand;

public interface AuthService {
    AuthResponse login(LoginCommand command);
    AuthResponse refresh(TokenRefreshRequest refreshToken);
    void logout();
}
