package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.model.Category;
import com.lopezinho.friendly_expense_tracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository; }

    public List<Category> findByUserId(UUID userId) { return categoryRepository.findByUserId(userId); }

    public Optional<Category> findById(UUID id) { return categoryRepository.findById(id); }

    public Category save(Category category) { return categoryRepository.save(category); }

    public void deleteById(UUID id) { categoryRepository.deleteById(id); }

    public Category update(UUID id, Category updatedData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(updatedData.getName());
        category.setType(updatedData.getType());
        category.setIcon(updatedData.getIcon());

        return categoryRepository.save(category);
    }
}