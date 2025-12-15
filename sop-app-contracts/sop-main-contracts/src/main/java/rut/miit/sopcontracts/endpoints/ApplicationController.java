package rut.miit.sopcontracts.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ApplicationRequest;
import rut.miit.sopcontracts.dto.request.ApplicationUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.StatusResponse;

import java.util.UUID;

@Tag(name = "applications", description = "API for managing loan applications")
@RequestMapping("/api/applications")
public interface ApplicationController {

    @Operation(summary = "Add a new credit application", description = "Creates a new credit application with the details provided in the request body")
    @ApiResponse(responseCode = "201", description = "Application successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<ApplicationResponse>> addApplication(@Valid @RequestBody ApplicationRequest applicationRequest);

    @Operation(summary = "Update an existing application", description = "Updates credit application details by ID using the provided request body")
    @ApiResponse(responseCode = "200", description = "Application successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<ApplicationResponse> updateApplication(@PathVariable("id") UUID id, @Valid @RequestBody ApplicationUpdateRequest applicationUpdateRequest);

    @Operation(summary = "Get credit application by ID", description = "Retrieves a specific credit application by its unique ID")
    @ApiResponse(responseCode = "200", description = "Application retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<ApplicationResponse> getApplication(@PathVariable("id") UUID id);

    @Operation(summary = "Get a list of all applications with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "Applications retrieved successfully")
    @GetMapping
    PagedModel<EntityModel<ApplicationResponse>> getAllApplications(
            @Parameter(description = "Page number (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by application status (REVIEWING|APPROVED|REJECTED)") @RequestParam(required = false) String status);


    @Operation(summary = "Get applications by client ID", description = "Retrieves all applications associated with a specific client")
    @ApiResponse(responseCode = "200", description = "Applications retrieved successfully")
    @GetMapping("/client/{clientId}")
    CollectionModel<EntityModel<ApplicationResponse>> getApplicationsByClient(@PathVariable("clientId") UUID clientId);

    @Operation(summary = "Logically delete an application", description = "Performs a logical deletion of an existing credit application by its ID")
    @ApiResponse(responseCode = "204", description = "Application successfully deleted")
    @ApiResponse(responseCode = "400", description = "Business validation failed", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Application not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteApplication(@PathVariable("id") UUID id);

}