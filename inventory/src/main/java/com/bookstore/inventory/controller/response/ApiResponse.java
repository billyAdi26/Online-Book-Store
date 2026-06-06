package com.bookstore.inventory.controller.response;

public record ApiResponse<T>(int status, T data, String message) {
}
