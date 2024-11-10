package com.yl3k.kbsf.global.response.exception;

import com.yl3k.kbsf.global.response.error.ApplicationError;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ApplicationError error;

    public ApplicationException(ApplicationError error) {

        super(error.getMessage());
        this.error = error;
    }
}
