package com.bewize.monitorbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> data;
    private Meta meta;

    public PageResponse(List<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public Meta(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

    }

}
