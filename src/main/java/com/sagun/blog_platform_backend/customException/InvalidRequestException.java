package com.sagun.blog_platform_backend.customException;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message){
        super(message);
    }

    public InvalidRequestException(){}

}
