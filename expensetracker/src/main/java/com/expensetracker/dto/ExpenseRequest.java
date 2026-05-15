package com.expensetracker.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @NotNull(message = "Expense date must not be null")
    @PastOrPresent(message = "Expense date cannot be a future date")
    private LocalDate expenseDate;

    @NotNull(message = "Category ID must not be null")
    private Long categoryId;
}
