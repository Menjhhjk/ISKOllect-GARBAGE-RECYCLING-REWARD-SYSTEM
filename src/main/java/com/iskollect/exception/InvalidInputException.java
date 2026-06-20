package com.iskollect.exception;

//for invalid inputs (empty fields or wrong formats)
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}