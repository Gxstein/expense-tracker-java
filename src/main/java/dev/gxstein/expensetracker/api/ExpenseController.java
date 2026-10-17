package dev.gxstein.expensetracker.api;

import dev.gxstein.expensetracker.api.dto.ExpenseRequest;
import dev.gxstein.expensetracker.api.dto.ExpenseResponse;
import dev.gxstein.expensetracker.api.dto.MonthlySummaryResponse;
import dev.gxstein.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.create(ownerId(jwt), request);
    }

    @GetMapping
    public List<ExpenseResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return expenseService.list(ownerId(jwt), month, year);
    }

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse summarize(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return expenseService.summarize(ownerId(jwt), month, year);
    }

    @GetMapping("/{id}")
    public ExpenseResponse findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return expenseService.findById(ownerId(jwt), id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(ownerId(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        expenseService.delete(ownerId(jwt), id);
    }

    private Long ownerId(Jwt jwt) {
        return ((Number) jwt.getClaim("userId")).longValue();
    }
}
