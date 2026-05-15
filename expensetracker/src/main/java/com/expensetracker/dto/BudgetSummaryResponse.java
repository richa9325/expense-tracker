package com.expensetracker.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSummaryResponse {
    private Long categoryId;
    private String categoryName;
    private BigDecimal monthlyBudget;
    private BigDecimal totalSpent;
    private BigDecimal remainingBudget;
    private String status;
}
