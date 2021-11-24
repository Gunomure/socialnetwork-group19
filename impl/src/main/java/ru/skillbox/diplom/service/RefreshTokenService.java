package ru.skillbox.diplom.service;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.exception.TokenRefreshException;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.repository.RefreshTokenRepository;
import ru.skillbox.diplom.repository.UserRepository;

import javax.naming.NamingException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${values.jwt.refreshExpirationDateInMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LdapService ldapService;

    public Optional<RefreshToken> findByToken(String token) throws NamingException {
        //return Optional.ofNullable(refreshTokenMap.get(token));
        return refreshTokenRepository.findByToken(token);
        //ldapService.searchUserField("sn", token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenToLdap(refreshToken);
        return refreshToken;
    }

    private void refreshTokenToLdap(RefreshToken refreshToken){
        ldapService.updateUserField(refreshToken.getUser().getEmail(),
                "cn", refreshToken.getToken());
        ldapService.updateUserField(refreshToken.getUser().getEmail(),
                "description", refreshToken.getExpiryDate().toString());
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            ldapService.updateUserField(token.getUser().getEmail(), "sn", "0");
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
