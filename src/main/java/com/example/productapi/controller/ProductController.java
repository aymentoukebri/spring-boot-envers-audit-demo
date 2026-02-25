package com.example.productapi.controller;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.dto.ProductRevisionResponse;
import com.example.productapi.service.ProductRevisionService;
import com.example.productapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management API")
public class ProductController {

    private final ProductService productService;
    private final ProductRevisionService productRevisionService;

    @GetMapping
    @Operation(summary = "Get all products")
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/{id}/revisions")
    @Operation(summary = "Get revision history for a product",
            description = "Returns a paginated list of all audit revisions for a given product, ordered by most recent first.")
    public Page<ProductRevisionResponse> getRevisions(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        return productRevisionService.getRevisions(id, pageable);
    }
}
