package com.example.productapi.service;

import com.example.productapi.dto.ProductRevisionResponse;
import com.example.productapi.entity.CustomRevisionEntity;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRevisionService {

    private final EntityManager entityManager;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductRevisionResponse> getRevisions(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        long totalRevisions = (long) auditReader.createQuery()
                .forRevisionsOfEntity(Product.class, false, true)
                .add(AuditEntity.id().eq(productId))
                .addProjection(AuditEntity.revisionNumber().count())
                .getSingleResult();

        @SuppressWarnings("unchecked")
        List<Object[]> results = auditReader.createQuery()
                .forRevisionsOfEntity(Product.class, false, true)
                .add(AuditEntity.id().eq(productId))
                .addOrder(AuditEntity.revisionNumber().desc())
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<ProductRevisionResponse> revisions = results.stream()
                .map(this::mapToRevisionResponse)
                .toList();

        return new PageImpl<>(revisions, pageable, totalRevisions);
    }

    private ProductRevisionResponse mapToRevisionResponse(Object[] row) {
        Product product = (Product) row[0];
        CustomRevisionEntity revisionEntity = (CustomRevisionEntity) row[1];
        RevisionType revisionType = (RevisionType) row[2];

        return ProductRevisionResponse.builder()
                .revisionNumber(revisionEntity.getId())
                .revisionType(revisionType.name())
                .revisionTimestamp(Instant.ofEpochMilli(revisionEntity.getTimestamp()))
                .product(productMapper.toResponse(product))
                .build();
    }
}
