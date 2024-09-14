package com.pramod.dreamshops.service.product;

import com.pramod.dreamshops.dto.ImageDto;
import com.pramod.dreamshops.dto.ProductDto;
import com.pramod.dreamshops.exception.AlreadyExistsException;
import com.pramod.dreamshops.exception.ProductNotFoundException;
import com.pramod.dreamshops.model.Category;
import com.pramod.dreamshops.model.Image;
import com.pramod.dreamshops.model.Product;
import com.pramod.dreamshops.repository.CategoryRepository;
import com.pramod.dreamshops.repository.ImageRepository;
import com.pramod.dreamshops.repository.ProductRepository;
import com.pramod.dreamshops.request.AddProductRequest;
import com.pramod.dreamshops.request.ProductUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.imageRepository = imageRepository;
    }

    @Override
    public Product addProduct(AddProductRequest request) {
        if (productExists(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName() + " already exists");
        }

        Category category = getOrSaveCategory(request.getCategory().getName());
        request.setCategory(category);
        return this.productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand) {
        return this.productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );

    }

    @Override
    public Product getProductById(Long id) {
        return this.productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not Found"));
    }

    @Override
    public void deleteProductById(Long id) {
        Optional<Product> byId = this.productRepository.findById(id);
        if (byId.isPresent()) {
            this.productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product Not Found");
        }
    }

    @Override
    public Product updateProduct(ProductUpdateRequest product, Long productId) {
        Optional<Product> optionalProduct = this.productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product existingProduct = updateExistingProduct(optionalProduct.get(), product);
            return this.productRepository.save(existingProduct);
        } else {
            throw new ProductNotFoundException("Product Not Found");
        }
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());
        Category category = getOrSaveCategory(request.getCategory().getName());
        existingProduct.setCategory(category);

        return existingProduct;
    }

    private Category getOrSaveCategory(String categoryName) {
        Optional<Category> optionalCategory = this.categoryRepository.findByName(categoryName);
        Category category = null;
        if (optionalCategory.isPresent()) {
            category = optionalCategory.get();
        } else {
            category = new Category(categoryName);
            this.categoryRepository.save(category);
        }

        return category;
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return this.productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return this.productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return this.productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return this.productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return this.productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return this.productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = this.imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> this.modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);

        return productDto;
    }

}
