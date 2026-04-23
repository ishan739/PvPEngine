package com.example.pvpengine.apiKey;

import com.example.pvpengine.apiKey.dto.ApiKeyResponse;
import com.example.pvpengine.apiKey.dto.GenerateApiKeyRequest;
import com.example.pvpengine.common.exception.PvpException;
import com.example.pvpengine.game.GameServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService{

    private static final String KEY_PREFIX_NAMESPACE = "pvp_live_";
    private static final int PREFIX_RANDOM_BYTES = 8;
    private static final int SECRET_RANDOM_BYTES = 32;
    private final ApiKeyRepository apiKeyRepository;
    private final GameServiceImpl gameService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiKeyResponse generateKey(UUID gameId, GenerateApiKeyRequest request) {
        gameService.findOrThrow(gameId);

        String prefixRandom = randomUrlSafeBase64(PREFIX_RANDOM_BYTES);
        String keyPrefix = KEY_PREFIX_NAMESPACE + prefixRandom;

        String secret = randomUrlSafeBase64(SECRET_RANDOM_BYTES);

        //important
        String rawKey = keyPrefix + "." + secret;
        String keyHash = passwordEncoder.encode(rawKey);

        ApiKeyCredential credential = ApiKeyCredential.builder()
                .gameId(gameId)
                .keyHash(keyHash)
                .keyPrefix(keyPrefix)
                .active(true)
                .expiresAt(request != null ? request.getExpiresAt() : null)
                .build();

        ApiKeyCredential saved = apiKeyRepository.save(credential);
        log.info("Api key generated for gameId={}, prefix={}" , gameId, keyPrefix);

        return ApiKeyResponse.fromWithRawKey(saved , rawKey);
    }

    @Transactional
    @Override
    public ApiKeyResponse revokeKey(UUID gameId, UUID keyId) {
        ApiKeyCredential credential = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> PvpException.notFound("Api key not found: " + keyId));

        if (!credential.getGameId().equals(gameId)) {
            throw PvpException.forbidden("Api key does not belong to this game");
        }

        if (!credential.isActive()){
            throw PvpException.conflict("Api key is already revoked");
        }
        credential.setActive(false);
        log.info("Api key revoked: id={}, gameId={}", keyId, gameId);
        return ApiKeyResponse.from(apiKeyRepository.save(credential));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> listKeysForGame(UUID gameId) {
        return apiKeyRepository.findAllByGameId(gameId)
                .stream()
                .map(ApiKeyResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Optional<UUID> resolveGameId(String rawApiKey) {
        if(rawApiKey == null || rawApiKey.isEmpty()){
            return Optional.empty();
        }

        int dotIndex = rawApiKey.indexOf('.');
        if(dotIndex < 0){
            return Optional.empty();
        }

        String prefix = rawApiKey.substring(0, dotIndex);
        Optional<ApiKeyCredential> credentialOpt = apiKeyRepository.
                findByKeyPrefixAndActiveTrue(prefix);

        if (credentialOpt.isEmpty()){
            return Optional.empty();
        }

        ApiKeyCredential credential = credentialOpt.get();

        if (credential.isExpired()){
            log.warn("Expired Api key used: prefix={}" ,  prefix);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(rawApiKey, credential.getKeyHash())){
            log.warn("Invalid Api key secret for prefix={}" ,  prefix);
            return Optional.empty();
        }

        try{
            apiKeyRepository.updateLastUsedAt(credential.getId() , OffsetDateTime.now());
        } catch (Exception e){
            log.error("Failed to update lastUsedAt for key prefix={}" ,  prefix , e);
        }

        return Optional.of(credential.getGameId());
    }

    private String randomUrlSafeBase64(int byteLength) {
        byte[] bytes = new byte[byteLength];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
