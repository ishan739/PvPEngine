package com.example.pvpengine.game.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateWebhookRequest {

    @NotBlank(message = "Webhook Url is required")
    private String webhookUrl;
}