package com.example.categoryservice.exception;

public class LackStockException extends RuntimeException{
    private final String errorMessage;
    public LackStockException(String message){
        super(message);
        this.errorMessage=message;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

}
