package com.bookstore.catalog.controller.response;

public record PageMeta( int page,
                        int pageSize,
                        long totalItems,
                        int totalPages,
                        String sort,
                        boolean sortAsc) {
}
