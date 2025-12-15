package rut.miit.sopcontracts.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.OfferDecisionRequest;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.StatusResponse;

import java.util.UUID;

@Tag(name = "offers", description = "API for managing credit offers")
@RequestMapping("/api/offers")
public interface OfferController {

    @Operation(summary = "Get offer by ID", description = "Retrieves detailed information about a credit offer by its ID")
    @ApiResponse(responseCode = "200", description = "Offer successfully retrieved")
    @ApiResponse(responseCode = "404", description = "Offer not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<OfferResponse> getOffer(@PathVariable("id") UUID id);

    @Operation(summary = "Get all offers", description = "Retrieves a paginated list of all offers")
    @ApiResponse(responseCode = "200", description = "Offers retrieved successfully")
    @GetMapping
    PagedModel<EntityModel<OfferResponse>> getAllOffers(
            @Parameter(description = "Page number (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Get offers by application ID", description = "Retrieves all offers associated with a specific credit application")
    @ApiResponse(responseCode = "200", description = "Offers retrieved successfully")
    @GetMapping("/application/{applicationId}")
    CollectionModel<EntityModel<OfferResponse>> getOffersByApplication(@PathVariable("applicationId") UUID applicationId);

    @Operation(summary = "Get offers by product ID", description = "Retrieves all offers associated with a specific product")
    @ApiResponse(responseCode = "200", description = "Offers retrieved successfully")
    @GetMapping("/product/{productId}")
    CollectionModel<EntityModel<OfferResponse>> getOffersByProduct(@PathVariable("productId") UUID productId);

    @Operation(summary = "Update offer status", description = "Updates the status of a credit offer (e.g. ACCEPTED, REJECTED)")
    @ApiResponse(responseCode = "200", description = "Offer status successfully updated")
    @ApiResponse(responseCode = "400", description = "Business validation failed", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Offer not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{id}/status")
    EntityModel<OfferResponse> updateOfferStatus(@PathVariable("id") UUID id, @Valid @RequestBody OfferDecisionRequest request);
}
