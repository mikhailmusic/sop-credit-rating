package rut.miit.sopcreditrating.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ProductRequest;
import rut.miit.sopcontracts.dto.request.ProductUpdateRequest;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcontracts.endpoints.ProductController;
import rut.miit.sopcreditrating.assembler.ProductModelAssembler;
import rut.miit.sopcreditrating.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductControllerImpl implements ProductController {

    private ProductService productService;
    private ProductModelAssembler productModelAssembler;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setProductModelAssembler(ProductModelAssembler productModelAssembler) {
        this.productModelAssembler = productModelAssembler;
    }

    @Override
    @PostMapping
    public ResponseEntity<EntityModel<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        EntityModel<ProductResponse> model = productModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    @PutMapping("/{id}")
    public EntityModel<ProductResponse> updateProduct(@PathVariable("id") UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse updated = productService.updateProduct(id, request);
        return productModelAssembler.toModel(updated);
    }

    @Override
    @GetMapping("/{id}")
    public EntityModel<ProductResponse> getProduct(@PathVariable("id") UUID id) {
        ProductResponse product = productService.getProduct(id);
        return productModelAssembler.toModel(product);
    }

    @Override
    @GetMapping("/by-code/{code}")
    public EntityModel<ProductResponse> getProductByCode(@PathVariable("code") String code) {
        ProductResponse product = productService.getByCode(code);
        return productModelAssembler.toModel(product);
    }


    @Override
    @GetMapping
    public CollectionModel<EntityModel<ProductResponse>> getAllProducts(
            @RequestParam(value = "purpose", required = false) String purpose) {

        List<ProductResponse> list = (purpose == null || purpose.isBlank())
                ? productService.getAllProducts()
                : productService.getByPurpose(purpose);

        return productModelAssembler.toCollectionModel(list);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteLogicalProduct(id);
    }
}