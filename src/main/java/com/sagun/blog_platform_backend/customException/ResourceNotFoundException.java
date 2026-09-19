package com.sagun.blog_platform_backend.customException;

public class ResourceNotFoundException extends Exception {
    ResourceNotFoundException(String message){
        super(message);
    }

}
