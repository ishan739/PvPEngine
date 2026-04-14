package com.example.pvpengine.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PvpException extends RuntimeException {
    private final HttpStatus status;
    public PvpException(HttpStatus status , String message) {
        super(message);
        this.status = status;
    }

    public static PvpException notFound(String message) {
        return new PvpException(HttpStatus.NOT_FOUND, message);
    }
    public static PvpException badRequest(String message) {
        return new PvpException(HttpStatus.BAD_REQUEST,message);
    }

    public static PvpException conflict(String message) {
        return new PvpException(HttpStatus.CONFLICT,message);
    }

    public static PvpException forbidden(String message) {
        return new PvpException(HttpStatus.FORBIDDEN,message);
    }
}
