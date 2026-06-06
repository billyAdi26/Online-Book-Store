package com.bookstore.catalog.repository;

import com.bookstore.catalog.controller.request.BookFilterRequest;
import com.bookstore.catalog.entity.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {
    public static Specification<Book> withFilters(BookFilterRequest filter){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }

            if (filter.getAuthor() != null && !filter.getAuthor().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("author")),
                        "%" + filter.getAuthor().toLowerCase() + "%"));
            }

            if (filter.getIsbn() != null && !filter.getIsbn().isBlank()) {
                predicates.add(cb.equal(root.get("isbn"), filter.getIsbn()));
            }

            if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")),
                        filter.getCategory().toLowerCase()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
