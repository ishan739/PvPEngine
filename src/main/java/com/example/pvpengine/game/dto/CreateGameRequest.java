package com.example.pvpengine.game.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CreateGameRequest {

    @NotBlank(message = "Game name is required")
    @Size(min = 2 ,max = 100 , message = "Name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Game code is erquired")
    @Size(min = 2 , max = 50 , message = "Code must be 2-50 characters long")
    @Pattern(regexp = "^[a-z0-9-]+$" , message = "code must be lowercase alphanumeric with hyphens only")
    private String code;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    private String webhookUrl;
}
