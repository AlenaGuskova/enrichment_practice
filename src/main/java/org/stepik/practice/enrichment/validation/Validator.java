package org.stepik.practice.enrichment.validation;

interface Validator<T> {

    void validate(T toBeValidate);
}
