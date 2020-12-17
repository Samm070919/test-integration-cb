package com.pagatodoholdings.posandroid.general.exceptions;

public class ClaveIncorrectaException extends Exception {

    public ClaveIncorrectaException(final String message) {
        super(message);
    }

    public ClaveIncorrectaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
