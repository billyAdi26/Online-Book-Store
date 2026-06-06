package com.bookstore.catalog.controller;

import com.bookstore.catalog.controller.request.BookFilterRequest;
import com.bookstore.catalog.controller.response.ApiResponse;
import com.bookstore.catalog.dto.BookDTO;
import com.bookstore.catalog.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDTO>>> search(@RequestBody BookFilterRequest filter) {
        return ResponseEntity.ok(bookService.search(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getById(@PathVariable UUID id){
        BookDTO data = bookService.getById(id);
        ApiResponse<BookDTO> responseBody = new ApiResponse<>(200, data, null);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> create(@RequestBody BookDTO dto){
        BookDTO data = bookService.save(dto);
        ApiResponse<BookDTO> responseBody = new ApiResponse<>(201, data, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> update(@PathVariable UUID id, @RequestBody BookDTO dto){
        BookDTO data = bookService.update(dto,id);
        ApiResponse<BookDTO> responseBody = new ApiResponse<>(200, data, null);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
