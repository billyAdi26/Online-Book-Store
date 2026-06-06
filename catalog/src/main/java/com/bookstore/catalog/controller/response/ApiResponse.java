package com.bookstore.catalog.controller.response;

public record ApiResponse<T>(int status,
                             T data,
                             String message,
                             PageMeta page) {
    public ApiResponse(int status, T data, String message) {
        this(status, data, message, null);
    }
}
