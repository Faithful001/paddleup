package com.king.paddleup.domain.category;

import com.king.paddleup.domain.category.dto.CreateCategoryRequest;
import com.king.paddleup.domain.category.dto.UpdateCategoryRequest;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Category create(CreateCategoryRequest payload) {
        Category category = Category.builder()
                .name(payload.name())
                .description(payload.description())
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("Category not found"));
    }

    @Transactional
    public Category update(UUID id, UpdateCategoryRequest payload) {
        Category category = findById(id);
        category.setName(payload.name());
        category.setDescription(payload.description());
        return category;
    }

    @Transactional
    public void delete(UUID id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }

}
