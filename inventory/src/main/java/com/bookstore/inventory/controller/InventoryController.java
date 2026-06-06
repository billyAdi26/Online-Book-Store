package com.bookstore.inventory.controller;

import com.bookstore.inventory.controller.request.StockOperationRequest;
import com.bookstore.inventory.controller.response.ApiResponse;
import com.bookstore.inventory.controller.response.StockCheckResponse;
import com.bookstore.inventory.dto.InventoryDTO;
import com.bookstore.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryDTO>> create(@RequestBody InventoryDTO dto) {
        InventoryDTO data = inventoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, data, null));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getByBookId(@PathVariable UUID bookId) {
        InventoryDTO data = inventoryService.getByBookId(bookId);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @PutMapping("/{bookId}/restock")
    public ResponseEntity<ApiResponse<InventoryDTO>> restock(
            @PathVariable UUID bookId,
            @RequestBody StockOperationRequest request) {
        InventoryDTO data = inventoryService.restock(bookId, request.quantity());
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @PostMapping("/{bookId}/check")
    public ResponseEntity<ApiResponse<StockCheckResponse>> checkStock(
            @PathVariable UUID bookId,
            @RequestBody StockOperationRequest request) {
        StockCheckResponse data = inventoryService.checkStock(bookId, request.quantity());
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @PostMapping("/{bookId}/reserve")
    public ResponseEntity<ApiResponse<InventoryDTO>> reserveStock(
            @PathVariable UUID bookId,
            @RequestBody StockOperationRequest request) {
        InventoryDTO data = inventoryService.reserveStock(bookId, request.quantity());
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @PostMapping("/{bookId}/release")
    public ResponseEntity<ApiResponse<InventoryDTO>> releaseStock(
            @PathVariable UUID bookId,
            @RequestBody StockOperationRequest request) {
        InventoryDTO data = inventoryService.releaseStock(bookId, request.quantity());
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStock(
            @RequestParam(defaultValue = "5") int threshold) {
        List<InventoryDTO> data = inventoryService.getLowStockItems(threshold);
        return ResponseEntity.ok(new ApiResponse<>(200, data, null));
    }

}
