package rut.miit.sopcreditrating.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ClientRequest;
import rut.miit.sopcontracts.dto.request.ClientUpdateRequest;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.endpoints.ClientController;
import rut.miit.sopcreditrating.assembler.ClientModelAssembler;
import rut.miit.sopcreditrating.service.ClientService;
import rut.miit.sopcreditrating.service.ClientStatisticsService;

import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientControllerImpl implements ClientController {

    private ClientService clientService;
    private ClientStatisticsService clientStatisticsService;
    private ClientModelAssembler clientModelAssembler;
    private PagedResourcesAssembler<ClientResponse> pagedResourcesAssembler;

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    @Autowired
    public void setClientStatisticsService(ClientStatisticsService clientStatisticsService) {this.clientStatisticsService = clientStatisticsService;}

    @Autowired
    public void setClientModelAssembler(ClientModelAssembler clientModelAssembler) {
        this.clientModelAssembler = clientModelAssembler;
    }
    @Autowired
    public void setPagedResourcesAssembler(PagedResourcesAssembler<ClientResponse> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    @PostMapping
    public ResponseEntity<EntityModel<ClientResponse>> addClient(@Valid @RequestBody ClientRequest clientRequest) {
        ClientResponse client = clientService.addClient(clientRequest);
        EntityModel<ClientResponse> entityModel = clientModelAssembler.toModel(client);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    @PutMapping(path = "/{id}")
    public EntityModel<ClientResponse> updateClient(@PathVariable("id") UUID id, @Valid @RequestBody ClientUpdateRequest clientUpdateRequest) {
        ClientResponse client = clientService.updateClient(id, clientUpdateRequest);
        return clientModelAssembler.toModel(client);
    }

    @Override
    @GetMapping(path = "/{id}")
    public EntityModel<ClientResponse> getClientById(@PathVariable("id") UUID id) {
        ClientResponse client = clientService.getClient(id);
        return clientModelAssembler.toModel(client);
    }

    @Override
    @GetMapping("/by-cif/{cif}")
    public EntityModel<ClientResponse> getClientByCif(@PathVariable("cif") String cif) {
        ClientResponse client = clientService.getClientByCif(cif);
        return clientModelAssembler.toModel(client);
    }

    @Override
    @GetMapping("/search")
    public EntityModel<ClientResponse> getClientByEmail(@RequestParam("email") String email) {
        ClientResponse client = clientService.getClientByEmail(email);
        return clientModelAssembler.toModel(client);
    }

    @Override
    @GetMapping("/{id}/statistics")
    public EntityModel<ClientStatisticsResponse> getClientStatistics(@PathVariable("id") UUID id) {
        ClientStatisticsResponse statistics = clientStatisticsService.calculateStatistics(id);
        return clientModelAssembler.toStatisticsModel(statistics);
    }


    @Override
    @GetMapping
    public PagedModel<EntityModel<ClientResponse>> getAllClients(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PagedResponse<ClientResponse> pagedResponse = clientService.getAllClients(page, size);
        Page<ClientResponse> clientPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedResourcesAssembler.toModel(clientPage, clientModelAssembler);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable("id") UUID id) {
        clientService.deleteLogicalClient(id);
    }
}
