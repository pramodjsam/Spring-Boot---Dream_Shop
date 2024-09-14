package com.pramod.dreamshops.service.order;

import com.pramod.dreamshops.dto.OrderDto;
import com.pramod.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
