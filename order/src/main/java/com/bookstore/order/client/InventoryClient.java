package com.bookstore.order.client;

import com.bookstore.order.controller.request.StockOperationRequest;
import com.bookstore.order.controller.response.StockCheckResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class InventoryClient {
    private final RestClient restClient;

    public InventoryClient(RestClient inventoryRestClient) {
        this.restClient = inventoryRestClient;
    }

    public StockCheckResponse checkStock(UUID bookId, int quantity) {
        return restClient.post()
                .uri("/api/v1/inventories/{bookId}/check", bookId)
                .body(new StockOperationRequest(quantity))
                .retrieve()
                .body(StockCheckResponse.class);
    }

    public void reserveStock(UUID bookId, int quantity) {
        restClient.post()
                .uri("/api/v1/inventories/{bookId}/reserve", bookId)
                .body(new StockOperationRequest(quantity))
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseStock(UUID bookId, int quantity) {
        restClient.post()
                .uri("/api/v1/inventories/{bookId}/release", bookId)
                .body(new StockOperationRequest(quantity))
                .retrieve()
                .toBodilessEntity();
    }

}
