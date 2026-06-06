package com.bookstore.order.controller.response;

public record ApiResponse<T>(int status, T data, String message) {
}
