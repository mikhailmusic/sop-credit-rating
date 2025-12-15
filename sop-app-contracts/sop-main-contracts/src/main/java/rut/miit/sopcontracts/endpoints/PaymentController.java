package rut.miit.sopcontracts.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.PaymentStatusUpdateRequest;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcontracts.dto.response.StatusResponse;

import java.util.UUID;

@Tag(name = "payments", description = "API for managing payments associated with offers")
@RequestMapping("/api/payments")
public interface PaymentController {

    @Operation(summary = "Get all payments for an offer", description = "Returns the list of payments associated with the specified offer ID")
    @ApiResponse(responseCode = "200", description = "Payments successfully retrieved")
    @ApiResponse(responseCode = "404", description = "Offer not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/offer/{offerId}")
    CollectionModel<EntityModel<PaymentResponse>> getPaymentsByOffer(@PathVariable("offerId") UUID offerId);

    @Operation(summary = "Update payment status", description = "Changes the status of a payment (e.g., PLANNED → COMPLETED / FAILED / DELAYED / CANCELED)")
    @ApiResponse(responseCode = "200", description = "Payment status successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input or invalid status transition", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{paymentId}/status")
    EntityModel<PaymentResponse> updatePaymentStatus(@PathVariable("paymentId") UUID paymentId, @Valid @RequestBody PaymentStatusUpdateRequest paymentStatusUpdateRequest);

    @Operation(summary = "Get a payment by ID", description = "Retrieves a single payment by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Payment successfully retrieved")
    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{paymentId}")
    EntityModel<PaymentResponse> getPayment(@PathVariable("paymentId") UUID paymentId);

    @Operation(summary = "Get payment by reference")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/by-reference/{reference}")
    EntityModel<PaymentResponse> getPaymentByReference(@PathVariable("reference") String reference);


    @Operation(summary = "Get payments by client ID", description = "Retrieves all payments associated with a specific client")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping("/client/{clientId}")
    CollectionModel<EntityModel<PaymentResponse>> getPaymentsByClient(@PathVariable("clientId") UUID clientId);

    @Operation(summary = "Get a list of all payments with pagination")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping
    PagedModel<EntityModel<PaymentResponse>> getAllPayments(
            @Parameter(description = "Page number (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    );
}
