package com.bookstore.inventory.repository;

import com.bookstore.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByBookId(UUID bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.bookId = :bookId")
    Optional<Inventory> findByBookIdWithLock(@Param("bookId") UUID bookId);

    @Query(value = """
            SELECT * FROM inventories
            WHERE (quantity - reserved_quantity) <= :threshold
            ORDER BY (quantity - reserved_quantity) ASC
            """, nativeQuery = true)
    List<Inventory> findLowStockItems(@Param("threshold") int threshold);
}
