package com.bookstore.inventory.service;

import com.bookstore.inventory.controller.response.StockCheckResponse;
import com.bookstore.inventory.dto.InventoryDTO;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
  InventoryDTO create(InventoryDTO dto);
  InventoryDTO getByBookId(UUID bookId);

  InventoryDTO restock(UUID bookId, int quantity);

  StockCheckResponse checkStock(UUID bookId, int quantity);
  InventoryDTO reserveStock(UUID bookId, int quantity);
  InventoryDTO releaseStock(UUID bookId, int quantity);
  List<InventoryDTO> getLowStockItems(int threshold);
}
