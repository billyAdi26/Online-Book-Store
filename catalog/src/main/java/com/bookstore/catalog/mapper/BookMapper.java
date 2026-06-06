package com.bookstore.catalog.mapper;

import com.bookstore.catalog.dto.BookDTO;
import com.bookstore.catalog.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toEntity(BookDTO dto);
    BookDTO toDto(Book entity);

    List<Book> toEntityList(List<BookDTO> dto);
    List<BookDTO> toDtoList(List<Book> entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BookDTO dto, @MappingTarget Book entity);
}
