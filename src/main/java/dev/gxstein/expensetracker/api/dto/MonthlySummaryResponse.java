package dev.gxstein.expensetracker.api.dto;

import java.math.BigDecimal;
public record MonthlySummaryResponse(String month, long expenseCount, BigDecimal total) {
}
