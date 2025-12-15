package rut.miit.sopcreditrating.service;

import rut.miit.sopcontracts.dto.request.ProductRequest;
import rut.miit.sopcontracts.dto.request.ProductUpdateRequest;
import rut.miit.sopcontracts.dto.response.ProductResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(UUID id, ProductUpdateRequest request);

    ProductResponse getProduct(UUID id);
    ProductResponse getByCode(String code);

    List<ProductResponse> getAllProducts();
    List<ProductResponse> getAllProducts(boolean active);
    List<ProductResponse> getByPurpose(String purpose);
    List<ProductResponse> getByPurpose(String purpose, boolean active);

    List<ProductResponse> getByIds(Set<UUID> ids, boolean active);

    void deleteLogicalProduct(UUID id);
}

