package com.king.paddleup.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateCategoryRequest(
        @NotBlank @Size(max = 100, message = "Max name length is 100") String name,
        @Size(max = 500, message = "Max description length is 500") String description
) {}
