package com.king.paddleup.domain.category;

import com.king.paddleup.domain.category.dto.CreateCategoryRequest;
import com.king.paddleup.domain.category.dto.UpdateCategoryRequest;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Response<Category>> create(
            @Valid @RequestBody CreateCategoryRequest payload
    ) {
        return ResponseEntity.ok()
                .body(Response.success(categoryService.create(payload)));
    }

    @GetMapping
    public ResponseEntity<Response<List<Category>>> findAll() {
        return ResponseEntity.ok()
                .body(Response.success(categoryService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Category>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .body(Response.success(categoryService.findById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<Category>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest payload
            ) {
        return ResponseEntity.ok()
                .body(Response.success(categoryService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> delete(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest payload
    ) {
        categoryService.delete(id);
        return ResponseEntity.ok()
                .body(Response.success());
    }
}
