package com.sagun.blog_platform_backend.customException;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
