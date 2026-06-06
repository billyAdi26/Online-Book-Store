package com.bookstore.catalog.service;

import com.bookstore.catalog.controller.request.BookFilterRequest;
import com.bookstore.catalog.controller.response.ApiResponse;
import com.bookstore.catalog.dto.BookDTO;

import java.util.List;
import java.util.UUID;

public interface BookService {

    ApiResponse<List<BookDTO>> search(BookFilterRequest filter);

    BookDTO save(BookDTO dto);

    BookDTO update(BookDTO dto, UUID id);

    BookDTO getById(UUID id);

    void deleteById(UUID id);
}
