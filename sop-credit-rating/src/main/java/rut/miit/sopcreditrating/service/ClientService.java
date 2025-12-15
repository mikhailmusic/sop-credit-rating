package rut.miit.sopcreditrating.service;

import rut.miit.sopcontracts.dto.request.ClientRequest;
import rut.miit.sopcontracts.dto.request.ClientUpdateRequest;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ClientService {

    ClientResponse addClient(ClientRequest clientRequest);

    ClientResponse updateClient(UUID id, ClientUpdateRequest clientUpdateRequest);

    ClientResponse getClient(UUID id);

    ClientResponse getClientByCif(String cif);

    ClientResponse getClientByEmail(String email);

    PagedResponse<ClientResponse> getAllClients(int page, int size);
    PagedResponse<ClientResponse> getAllClients(int page, int size, boolean active);

    List<ClientResponse> getByIds(Set<UUID> ids, boolean active);

    void deleteLogicalClient(UUID id);
}
