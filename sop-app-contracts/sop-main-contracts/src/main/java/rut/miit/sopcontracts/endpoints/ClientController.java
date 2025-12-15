package rut.miit.sopcontracts.endpoints;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ClientUpdateRequest;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.request.ClientRequest;
import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;
import rut.miit.sopcontracts.dto.response.StatusResponse;

import java.util.UUID;

@Tag(name = "clients", description = "API for managing clients")
@RequestMapping("/api/clients")
public interface ClientController {

    @Operation(summary = "Add a new client", description = "Creates a new client with the details provided in the request body")
    @ApiResponse(responseCode = "201", description = "Client successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Client with the given CIF already exists", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<ClientResponse>> addClient(@Valid @RequestBody ClientRequest clientRequest);


    @Operation(summary = "Update an existing client", description = "Updates client details by ID using the provided request body")
    @ApiResponse(responseCode = "200", description = "Client successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<ClientResponse> updateClient(@PathVariable("id") UUID id, @Valid @RequestBody ClientUpdateRequest clientUpdateRequest);


    @Operation(summary = "Get client by ID", description = "Retrieves details of a client by their unique ID")
    @ApiResponse(responseCode = "200", description = "Client details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<ClientResponse> getClientById(@PathVariable("id") UUID id);

    @Operation(summary = "Get client by CIF")
    @ApiResponse(responseCode = "200", description = "Client found")
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/by-cif/{cif}")
    EntityModel<ClientResponse> getClientByCif(@PathVariable("cif") String cif);

    @Operation(summary = "Get client by email")
    @ApiResponse(responseCode = "200", description = "Client found")
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/search")
    EntityModel<ClientResponse> getClientByEmail(@RequestParam("email") String email);


    @Operation(summary = "Get a list of all clients with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "Clients retrieved successfully")
    @GetMapping
    PagedModel<EntityModel<ClientResponse>> getAllClients(
            @Parameter(description = "Page number (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size);


    @Operation(summary = "Logically delete a client", description = "Performs a logical deletion of the client by its ID")
    @ApiResponse(responseCode = "204", description = "Client successfully deleted (logical delete)")
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteClient(@PathVariable("id") UUID id);


    @Operation(summary = "Get client statistics", description = "Retrieves comprehensive credit statistics and history for a client")
    @ApiResponse(responseCode = "200", description = "Statistics calculated successfully")
    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}/statistics")
    EntityModel<ClientStatisticsResponse> getClientStatistics(@PathVariable("id") UUID id);

}
