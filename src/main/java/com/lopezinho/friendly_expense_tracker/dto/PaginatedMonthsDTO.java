package com.lopezinho.friendly_expense_tracker.dto;

import java.util.List;

public class PaginatedMonthsDTO {
    private List<MonthGroupDTO> content;
    private int currentPage;
    private int totalPages;
    private long totalMonths;

    public PaginatedMonthsDTO(List<MonthGroupDTO> content, int currentPage, int totalPages, long totalMonths) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalMonths = totalMonths;
    }

    public List<MonthGroupDTO> getContent() { return content; }
    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() { return totalPages; }
    public long getTotalMonths() { return totalMonths; }
}