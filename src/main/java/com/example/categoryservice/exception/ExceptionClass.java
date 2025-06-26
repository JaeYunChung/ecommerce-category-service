package com.example.categoryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionClass {
    @ExceptionHandler(value = LackStockException.class)
    public ResponseEntity<?> manageLackStockException(LackStockException e){
        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                .body(e.getErrorMessage());
    }
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<?> manageIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
