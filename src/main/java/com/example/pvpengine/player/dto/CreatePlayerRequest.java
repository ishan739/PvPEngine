package com.example.pvpengine.player.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Request payload for creating a player in the authenticated game.")
public class CreatePlayerRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3 , max = 50 , message = "Username must be within 3-50 characters")
    @Schema(description = "Display username of the player.", example = "pro_striker")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Player contact email.", example = "player1@arena.gg")
    private String email;

    @Schema(
            description = "Optional external player identifier from your own game/user system. Use this when you already maintain player IDs outside PvPEngine.",
            example = "steam_76561198000000000"
    )
    private String externalPlayerId;
}
