package com.example.pvpengine.player.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePlayerRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3 , max = 50 , message = "Username must be within 3-50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String externalPlayerId;
}
