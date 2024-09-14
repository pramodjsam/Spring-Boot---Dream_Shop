package com.pramod.dreamshops.controller;

import com.pramod.dreamshops.model.Cart;
import com.pramod.dreamshops.model.User;
import com.pramod.dreamshops.response.ApiResponse;
import com.pramod.dreamshops.service.cart.ICartItemService;
import com.pramod.dreamshops.service.cart.ICartService;
import com.pramod.dreamshops.service.user.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

    public CartItemController(ICartItemService cartItemService, ICartService cartService, IUserService userService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(
//            @RequestParam(required = false) Long cartId,
                                                     @RequestParam Long productId,
                                                     @RequestParam Integer quantity) {
        try {
//            if (cartId == null) {
//                cartId = this.cartService.initializeNewCart();
//            }
            User user = this.userService.getAuthenticatedUser();
            if(user == null){
                throw new Error("User not found");
            }
            Cart cart = this.cartService.initializeNewCart(user);

            this.cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new ApiResponse("Add Item Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Add Item Failed", null));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(@RequestParam Long cartId, @RequestParam Long productId) {
        try {
            this.cartItemService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(new ApiResponse("Remove Item Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Remove Item Failed", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@RequestParam Long cartId, @RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            this.cartItemService.updateItemQuantity(cartId, productId, quantity);
            return ResponseEntity.ok(new ApiResponse("Update Item Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Update Item Failed", null));
        }
    }
}
