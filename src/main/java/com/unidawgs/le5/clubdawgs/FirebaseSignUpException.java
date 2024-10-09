package com.unidawgs.le5.clubdawgs;

public class FirebaseSignUpException extends Exception {
    private final String errorCode;

    public FirebaseSignUpException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}