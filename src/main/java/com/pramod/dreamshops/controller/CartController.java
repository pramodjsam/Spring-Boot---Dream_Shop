package com.pramod.dreamshops.controller;

import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.response.ApiResponse;
import com.pramod.dreamshops.service.cart.ICartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/id/{cartId}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId) {
        try {
            Cart cart = this.cartService.getCartById(cartId);
            return ResponseEntity.ok(new ApiResponse("Success", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Error", e.getMessage()));
        }
    }

    @DeleteMapping("/id/{cartId}")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        try {
            this.cartService.clearCartById(cartId);
            return ResponseEntity.ok(new ApiResponse("Clear Cart Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", e.getMessage()));
        }
    }

    @GetMapping("/total/id/{cartId}")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId){
        try{
            BigDecimal totalPrice = this.cartService.getTotalPriceById(cartId);
            return ResponseEntity.ok(new ApiResponse("Success", totalPrice));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error", e.getMessage()));
        }
    }
}
