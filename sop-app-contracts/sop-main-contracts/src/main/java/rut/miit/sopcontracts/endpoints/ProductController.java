package rut.miit.sopcontracts.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ProductRequest;
import rut.miit.sopcontracts.dto.request.ProductUpdateRequest;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcontracts.dto.response.StatusResponse;

import java.util.UUID;

@Tag(name = "products", description = "API for managing credit products")
@RequestMapping("/api/products")
public interface ProductController {

    @Operation(summary = "Create a new credit product", description = "Creates a new product available for credit offers")
    @ApiResponse(responseCode = "201", description = "Product successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request);

    @Operation(summary = "Update an existing product", description = "Updates product details by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Product successfully updated")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<ProductResponse> updateProduct(@PathVariable("id") UUID id, @Valid @RequestBody ProductUpdateRequest request);

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Product found successfully")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<ProductResponse> getProduct(@PathVariable("id") UUID id);

    @Operation(summary = "Get product by code")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/by-code/{code}")
    EntityModel<ProductResponse> getProductByCode(@PathVariable("code") String code);

    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products, optionally filtered by purpose")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    CollectionModel<EntityModel<ProductResponse>> getAllProducts(
            @RequestParam(value = "purpose", required = false) String purpose);

    @Operation(summary = "Logically delete a product", description = "Marks a product as inactive (logical deletion)")
    @ApiResponse(responseCode = "204", description = "Product successfully marked as inactive")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProduct(@PathVariable("id") UUID id);
}
