package com.bookstore.inventory.service;

import com.bookstore.inventory.controller.response.StockCheckResponse;
import com.bookstore.inventory.dto.InventoryDTO;
import com.bookstore.inventory.entity.Inventory;
import com.bookstore.inventory.exception.InsufficientStockException;
import com.bookstore.inventory.mapper.InventoryMapper;
import com.bookstore.inventory.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public InventoryDTO create(InventoryDTO dto) {
        Inventory savedEntity = inventoryRepository.save(inventoryMapper.toEntity(dto));
        return inventoryMapper.toDto(savedEntity);
    }

    @Override
    public InventoryDTO getByBookId(UUID bookId) {
        Inventory inventory = findByBookIdOrThrow(bookId);
        return inventoryMapper.toDto(inventory);
    }

    @Override
    public InventoryDTO restock(UUID bookId, int quantity) {
        Inventory inventory = findByBookIdOrThrow(bookId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public StockCheckResponse checkStock(UUID bookId, int quantity) {
        Inventory inventory = findByBookIdOrThrow(bookId);
        int available = inventory.getQuantity() - inventory.getReservedQuantity();
        return new StockCheckResponse(available >= quantity);
    }

    @Override
    @Transactional
    public InventoryDTO reserveStock(UUID bookId, int quantity) {
         Inventory inventory = inventoryRepository.findByBookIdWithLock(bookId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Inventory for bookId %s not found", bookId)));

        int available = inventory.getQuantity() - inventory.getReservedQuantity();
        if (available < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock for bookId %s. Requested: %d, Available: %d",
                            bookId, quantity, available));
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDTO releaseStock(UUID bookId, int quantity) {
        Inventory inventory = findByBookIdOrThrow(bookId);

        int newReserved = inventory.getReservedQuantity() - quantity;
        inventory.setReservedQuantity(Math.max(newReserved, 0));
        return inventoryMapper.toDto(inventoryRepository.save(inventory));
    }

    @Override
    public List<InventoryDTO> getLowStockItems(int threshold) {
        return inventoryRepository.findLowStockItems(threshold)
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    private Inventory findByBookIdOrThrow(UUID bookId) {
        return inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Inventory for bookId %s not found", bookId)));
    }


}
