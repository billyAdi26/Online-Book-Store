package com.bookstore.catalog.handler;

import com.bookstore.catalog.controller.response.ApiResponse;
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

}
