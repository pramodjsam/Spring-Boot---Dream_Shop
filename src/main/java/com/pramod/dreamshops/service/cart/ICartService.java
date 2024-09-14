package com.pramod.dreamshops.service.cart;

import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);

    void clearCartById(Long id);

    BigDecimal getTotalPriceById(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
