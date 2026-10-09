package com.sagun.blog_platform_backend.exceptionsHandler;


import com.sagun.blog_platform_backend.customException.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionsHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail optimisticLock(ObjectOptimisticLockingFailureException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"The resource was modified by someone else, Reload and try again");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integrity(DataIntegrityViolationException ex){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"The request conflicts with existing data.");
    }


}
