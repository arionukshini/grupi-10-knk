package com.company.system.exceptions;

public class InvalidSalaryException extends RuntimeException {
    public InvalidSalaryException(double salary) {
        super(salary + " is not a valid salary!");
    }
}
