package com.coronacarecard.mapper.validation.impl;

import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.validation.ValidateCondition;
import lombok.SneakyThrows;

public class NotNegativeValidation implements ValidateCondition {

    private int _value = 0;

    public NotNegativeValidation(int value) {
        _value = value;
    }

    @SneakyThrows
    @Override
    public void validate() {
        if(_value < 0) {
            throw new InternalException("Paged Business Search Result page number cannot be less than 0.");
        }
    }
}
