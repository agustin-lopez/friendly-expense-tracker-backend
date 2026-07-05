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

    public List<Category> findAll() { return categoryRepository.findAll(); }

    public Optional<Category> findById(UUID id) { return categoryRepository.findById(id); }

    public Category save(Category category) { return categoryRepository.save(category); }

    public void deleteById(UUID id) { categoryRepository.deleteById(id); }

}