package org.stepik.practice.enrichment.validation;

public class ValidationException extends RuntimeException {

    public ValidationException(final String s) {
        super(s);
    }

    public ValidationException(final String s, final Throwable e) {
        super(s, e);
    }
}
