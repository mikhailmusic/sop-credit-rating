package rut.miit.sopcreditrating.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.request.ProductRequest;
import rut.miit.sopcontracts.dto.request.ProductUpdateRequest;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.Product;
import rut.miit.sopcreditrating.entity.enums.Purpose;
import rut.miit.sopcreditrating.repository.ProductRepository;
import rut.miit.sopcreditrating.service.ProductService;
import rut.miit.sopcreditrating.util.EnumUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        productRepository.findByCode(request.code()).ifPresent(p -> {
            throw new BusinessLogicException("Product with code '" + request.code() + "' already exists");
        });
        Purpose purpose = EnumUtils.parseEnumOrThrow(Purpose.class, request.purpose(), "purpose");

        Product product = new Product(request.code(), request.name(), request.description(), request.version(),
                purpose, request.minAmount(), request.maxAmount(), request.minTermMonths(), request.maxTermMonths(),
                request.baseAprMin(), request.baseAprMax());

        productRepository.save(product);
        return toDto(product);
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive).orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setVersion(request.version());
        product.setMinAmount(request.minAmount());
        product.setMaxAmount(request.maxAmount());
        product.setMinTermMonths(request.minTermMonths());
        product.setMaxTermMonths(request.maxTermMonths());
        product.setBaseAprMin(request.baseAprMin());
        product.setBaseAprMax(request.baseAprMax());

        productRepository.save(product);
        return toDto(product);
    }

    @Override
    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toDto(product);
    }

    @Override
    public ProductResponse getByCode(String code) {
        Product p = productRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Product with code=" + code + " not found"));
        return toDto(p);
    }


    @Override
    public List<ProductResponse> getAllProducts() {
        boolean active = true;
        return getAllProducts(active);
    }

    @Override
    public List<ProductResponse> getAllProducts(boolean active) {
        return productRepository.findAll(active).stream().map(this::toDto).toList();
    }

    @Override
    public List<ProductResponse> getByPurpose(String purpose) {
        boolean active = true;
        return getByPurpose(purpose, active);
    }

    @Override
    public List<ProductResponse> getByPurpose(String purpose, boolean active) {
        Purpose parsedPurpose = EnumUtils.parseEnumOrThrow(Purpose.class, purpose, "purpose");

        List<Product> products = productRepository.findByPurpose(parsedPurpose, active);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No active products found for purpose: " + purpose);
        }

        return products.stream().map(this::toDto).toList();
    }

    @Override
    public void deleteLogicalProduct(UUID id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive).orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setActive(false);
        productRepository.save(product);
    }


    @Override
    public List<ProductResponse> getByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return productRepository.findByIds(ids, active).stream()
                .map(this::toDto).toList();
    }


    private ProductResponse toDto(Product product) {
        return new ProductResponse(
                product.getId(), product.getCode(), product.getName(), product.getDescription(), product.getVersion(),
                product.getPurpose().name(), product.getMinAmount(), product.getMaxAmount(), product.getMinTermMonths(),
                product.getMaxTermMonths(), product.getBaseAprMin(), product.getBaseAprMax(), product.isActive(),
                product.getCreatedAt(), product.getUpdatedAt()
        );
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
