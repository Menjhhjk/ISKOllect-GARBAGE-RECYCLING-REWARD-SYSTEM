package com.iskollect.exception;

//for when the user tries to redeem a coupon but does not have the sufficient points
public class InsufficientPointsException extends Exception {
    public InsufficientPointsException(String message) {
        super(message);
    }
}
