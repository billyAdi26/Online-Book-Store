package com.bookstore.inventory.handler;

import com.bookstore.inventory.controller.response.ApiResponse;
import com.bookstore.inventory.exception.InsufficientStockException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> entityNotFoundHandler(EntityNotFoundException ex){
        ApiResponse<Void> errorResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> runtimeExceptionHandler(RuntimeException ex){
        ApiResponse<Void> errorResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> insufficientStockHandler(InsufficientStockException ex) {
        ApiResponse<Void> errorResponse = new ApiResponse<>(HttpStatus.CONFLICT.value(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
