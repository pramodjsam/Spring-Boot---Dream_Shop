package com.pramod.dreamshops.service.cart;

import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.model.CartItem;
import com.pramod.dreamshops.model.Product;
import com.pramod.dreamshops.repository.CartItemRepository;
import com.pramod.dreamshops.repository.CartRepository;
import com.pramod.dreamshops.service.product.IProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;

    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository, IProductService productService, ICartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cartService = cartService;
    }

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = this.cartService.getCartById(cartId);
        Product product = this.productService.getProductById(productId);
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct()
                        .getId()
                        .equals(product.getId()))
                .findFirst().orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItem.setUnitPrice(product.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        this.cartItemRepository.save(cartItem);
        this.cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = this.cartService.getCartById(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        this.cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = this.cartService.getCartById(cartId);
        cart.getCartItems().stream().filter((item) -> item.getProduct().getId().equals(productId)).findFirst().ifPresent((item) -> {
            item.setQuantity(quantity);
            item.setUnitPrice(item.getProduct().getPrice());
            item.setTotalPrice();
        });
        BigDecimal totalAmount = cart.getCartItems().stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
        this.cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = this.cartService.getCartById(cartId);
        return cart.getCartItems().stream()
                .filter((item) -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }
}
