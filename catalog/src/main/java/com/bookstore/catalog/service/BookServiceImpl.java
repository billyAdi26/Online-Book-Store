package com.bookstore.catalog.service;

import com.bookstore.catalog.controller.request.BookFilterRequest;
import com.bookstore.catalog.controller.response.ApiResponse;
import com.bookstore.catalog.controller.response.PageMeta;
import com.bookstore.catalog.dto.BookDTO;
import com.bookstore.catalog.entity.Book;
import com.bookstore.catalog.mapper.BookMapper;
import com.bookstore.catalog.repository.BookRepository;
import com.bookstore.catalog.repository.BookSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository =  bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public ApiResponse<List<BookDTO>> search(BookFilterRequest filter) {
        Sort sort = filter.isSortAsc()
                ? Sort.by(filter.getSort()).ascending()
                : Sort.by(filter.getSort()).descending();

        Pageable pageable = PageRequest.of(filter.getPage() -1 , filter.getPageSize(), sort);
        Specification<Book> spec = BookSpecification.withFilters(filter);

        Page<Book> result = bookRepository.findAll(spec, pageable);

        PageMeta meta = new PageMeta(
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                filter.getSort(),
                filter.isSortAsc()
        );

        return new ApiResponse<>(200, bookMapper.toDtoList(result.getContent()), null, meta);
    }

    @Override
    public BookDTO save(BookDTO dto) {
        Book savedEntity = bookRepository.save(bookMapper.toEntity(dto));
        return bookMapper.toDto(savedEntity);
    }

    @Override
    public BookDTO update(BookDTO dto, UUID id) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(constructEntityNotFoundExceptionMessage(id)));

        bookMapper.updateEntityFromDto(dto, existing);

        return bookMapper.toDto(bookRepository.save(existing));
    }

    @Override
    public BookDTO getById(UUID id) {
        Book bookEntity = bookRepository.
                findById(id).
                orElseThrow(()->new EntityNotFoundException(constructEntityNotFoundExceptionMessage(id)));
        return bookMapper.toDto(bookEntity);
    }

    @Override
    public void deleteById(UUID id) {
        if(!bookRepository.existsById(id)) throw new EntityNotFoundException(constructEntityNotFoundExceptionMessage(id));

        bookRepository.deleteById(id);
    }

    private String constructEntityNotFoundExceptionMessage(UUID id){
        return String.format("Book with id %s not found", id);
    }
}
