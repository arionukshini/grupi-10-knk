package com.company.system.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super(email + " is not a valid email!");
    }
}
