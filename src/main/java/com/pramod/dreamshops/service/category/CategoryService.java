package com.pramod.dreamshops.service.category;

import com.pramod.dreamshops.exception.AlreadyExistsException;
import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.Category;
import com.pramod.dreamshops.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            throw new ResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Category getCategoryByName(String name) {
        Optional<Category> optionalCategory = this.categoryRepository.findByName(name);
        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            throw new ResourceNotFoundException("Category not found");
        }
    }

    @Override
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        Boolean existsByName = this.categoryRepository.existsByName(category.getName());
        if (existsByName) {
            throw new AlreadyExistsException(category.getName() + " already exists");
        } else {
            return this.categoryRepository.save(category);
        }
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category oldCategory = optionalCategory.get();
            oldCategory.setName(category.getName());
            return this.categoryRepository.save(oldCategory);
        } else {
            throw new ResourceNotFoundException("Category not found");
        }
    }

    @Override
    public void deleteCategoryById(Long id) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            this.categoryRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Category not found");
        }
    }
}
