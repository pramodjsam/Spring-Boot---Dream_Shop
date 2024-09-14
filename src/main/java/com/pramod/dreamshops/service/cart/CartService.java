package com.pramod.dreamshops.service.cart;

import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.model.User;
import com.pramod.dreamshops.repository.CartItemRepository;
import com.pramod.dreamshops.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AtomicLong cartIdGenerator = new AtomicLong(0);

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public Cart getCartById(Long id) {
        Cart cart = this.cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);

        return this.cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCartById(Long id) {
        Cart cart = this.getCartById(id);
        this.cartItemRepository.deleteAllByCartId(id);
        cart.getCartItems().clear();
        this.cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalPriceById(Long id) {
        Cart cart = getCartById(id);

        return cart.getTotalAmount();
    }

    @Override
    public Cart initializeNewCart(User user) {
        return Optional.ofNullable(this.getCartByUserId(user.getId())).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return this.cartRepository.save(cart);
        });
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return this.cartRepository.findByUserId(userId);
    }
}
