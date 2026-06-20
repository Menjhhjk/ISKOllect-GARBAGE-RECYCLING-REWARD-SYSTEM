package com.iskollect.exception;

//for navigation errors between scenes
public class NavigationException extends Exception {
    public NavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
