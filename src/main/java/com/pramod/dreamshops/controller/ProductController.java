package com.pramod.dreamshops.controller;

import com.pramod.dreamshops.dto.ProductDto;
import com.pramod.dreamshops.model.Product;
import com.pramod.dreamshops.request.AddProductRequest;
import com.pramod.dreamshops.request.ProductUpdateRequest;
import com.pramod.dreamshops.response.ApiResponse;
import com.pramod.dreamshops.service.product.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto convertedProduct = this.productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("success", convertedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody AddProductRequest addProductRequest) {
        try {
            Product theProduct = this.productService.addProduct(addProductRequest);
            ProductDto convertedProduct = this.productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("success", convertedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest, @PathVariable Long id) {
        try {
            Product theProduct = this.productService.updateProduct(productUpdateRequest, id);
            ProductDto convertedProduct = this.productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("success", convertedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        try {
            this.productService.deleteProductById(id);
            return ResponseEntity.ok(new ApiResponse("success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("/brand/{brand}/name/{name}")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@PathVariable String brand, @PathVariable String name) {
        try {
            List<Product> products = this.productService.getProductsByBrandAndName(brand, name);
            List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("/brand/{brand}/category/{category}")
    public ResponseEntity<ApiResponse> getProductByBrandAndCategory(@PathVariable String brand, @PathVariable String category) {
        try {
            List<Product> products = this.productService.getProductsByCategoryAndBrand(brand, category);
            List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("name/{name}")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
        try {
            List<Product> products = this.productService.getProductsByName(name);
            List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse> getProductByBrand(@PathVariable String brand) {
        try {
            List<Product> products = this.productService.getProductsByBrand(brand);
            List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getProductByCategory(@PathVariable String category) {
        try {
            List<Product> products = this.productService.getProductsByCategory(category);
            List<ProductDto> convertedProducts = this.productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }

    @GetMapping("/count/brand/{brand}/name/{name}")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@PathVariable String brand, @PathVariable String name) {
        try {
            Long productCount = this.productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product count!", productCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Not Found", e.getMessage()));
        }
    }
}
