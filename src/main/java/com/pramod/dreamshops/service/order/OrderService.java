package com.pramod.dreamshops.service.order;

import com.pramod.dreamshops.dto.OrderDto;
import com.pramod.dreamshops.enums.OrderStatus;
import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.model.Order;
import com.pramod.dreamshops.model.OrderItem;
import com.pramod.dreamshops.model.Product;
import com.pramod.dreamshops.repository.OrderRepository;
import com.pramod.dreamshops.repository.ProductRepository;
import com.pramod.dreamshops.service.cart.ICartService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ICartService cartService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Order placeOrder(Long userId) {
        Cart cart = this.cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = this.orderRepository.save(order);
        this.cartService.clearCartById(cart.getId());

        return savedOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());

        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getCartItems().stream().map((item) -> {
            Product product = item.getProduct();
            product.setInventory(product.getInventory() - item.getQuantity());
            this.productRepository.save(product);
            return new OrderItem(order, product, item.getQuantity(), item.getUnitPrice());
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map((item) -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public OrderDto getOrder(Long orderId) {
        return this.orderRepository.findById(orderId)
                .map((order) -> this.convertToDto(order))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = this.orderRepository.findByUserId(userId);
        return orders.stream().map((order) -> this.convertToDto(order)).toList();
//        return this.orderRepository.findByUserId(userId);
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
