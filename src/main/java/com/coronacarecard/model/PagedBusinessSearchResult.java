package com.coronacarecard.model;

import java.util.List;

@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Getter
public class PagedBusinessSearchResult {
    private List<BusinessSearchResult> items;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
}
