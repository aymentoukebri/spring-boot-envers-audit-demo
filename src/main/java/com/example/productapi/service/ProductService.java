package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(findProductById(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductById(id);
        productMapper.updateEntity(request, product);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
