package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class LockAcquisitionException extends RuntimeException {
    public LockAcquisitionException(String msg) {
        super(msg);
    }
}
