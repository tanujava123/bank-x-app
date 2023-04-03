package com.banking.app.exception;

public class GenericException extends RuntimeException{

    public GenericException() {
        super("Something went wrong");
    }
}
