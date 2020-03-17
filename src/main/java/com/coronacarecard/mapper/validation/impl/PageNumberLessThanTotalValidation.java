package com.coronacarecard.mapper.validation.impl;

import com.coronacarecard.exceptions.InternalException;
import com.coronacarecard.mapper.validation.ValidateCondition;
import lombok.SneakyThrows;

public class PageNumberLessThanTotalValidation implements ValidateCondition {

    private int _currentPageNumber;
    private int _totalPageNumber;

    public PageNumberLessThanTotalValidation(int currentPageNumber, int totalPageNumber) {
        _currentPageNumber = currentPageNumber;
        _totalPageNumber = totalPageNumber;
    }

    @SneakyThrows
    @Override
    public void validate() {
        if(_currentPageNumber > _totalPageNumber) {
            throw new InternalException("Paged Business Search Result page number cannot be greater than total number of pages.");
        }
    }
}
