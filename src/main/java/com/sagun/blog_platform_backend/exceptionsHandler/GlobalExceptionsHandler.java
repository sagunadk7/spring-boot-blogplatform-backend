package com.sagun.blog_platform_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exception){
        Map<String, Object> messages = new HashMap<>();
        messages.put("status", HttpStatus.BAD_REQUEST);
        messages.put("error","Invalid request");
        for(Map.Entry<String,Object> keyset: messages.entrySet()){
            System.out.println("This is keyset"+keyset);
        }
        return ResponseEntity.badRequest().body(messages);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException exception){
        return ResponseEntity.internalServerError().body(Map.of("status",HttpStatus.INTERNAL_SERVER_ERROR,"error","Internal Server error"));
    }

    @ExceptionHandler(I)

}
