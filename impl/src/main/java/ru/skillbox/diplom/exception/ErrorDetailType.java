package ru.skillbox.diplom.exception;

public enum ErrorDetailType {
    UNAUTHORIZED("Unauthorized"),
    SUPPLIED("An authorization code must be supplied"),
    REDIRECT_MISMATCH("Redirect URI mismatch"),
    INVALID_AUTH_CODE("Invalid authorization code: CODE"), //TODO how to set the CODE?
    BAD_CRED("Bad credentials");

    private final String errorType;

    ErrorDetailType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}
