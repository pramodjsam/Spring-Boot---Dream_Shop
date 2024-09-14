package com.pramod.dreamshops.controller;

import com.pramod.dreamshops.dto.OrderDto;
import com.pramod.dreamshops.model.Order;
import com.pramod.dreamshops.response.ApiResponse;
import com.pramod.dreamshops.service.order.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> createOrder(@PathVariable Long userId) {
        try {
            Order order = this.orderService.placeOrder(userId);
            OrderDto orderDto = this.orderService.convertToDto(order);
            return ResponseEntity.ok(new ApiResponse("Product Order Success", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error occurred", null));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = this.orderService.getOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Success", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error occurred", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> order = this.orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Success", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error occurred", null));
        }
    }
}
