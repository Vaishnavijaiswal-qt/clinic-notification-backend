package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventMappingPageResponse {

    private List<EventMappingResponse> content;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    public static class Pagination {

        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }
}