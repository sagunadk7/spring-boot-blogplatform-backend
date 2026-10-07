package com.sagun.blog_platform_backend.customException;

public class BlogDoesNotExistException extends RuntimeException{
    public BlogDoesNotExistException(String message){
        super(message);
    }
    public BlogDoesNotExistException(){
        super();
    }
}
