package com.lopezinho.friendly_expense_tracker.dto;

import java.math.BigDecimal;

public class CategoryTotalDTO {
    private String categoryName;
    private BigDecimal total;

    public CategoryTotalDTO(String categoryName, BigDecimal total) {
        this.categoryName = categoryName;
        this.total = total;
    }

    public String getCategoryName() { return categoryName; }
    public BigDecimal getTotal() { return total; }
}