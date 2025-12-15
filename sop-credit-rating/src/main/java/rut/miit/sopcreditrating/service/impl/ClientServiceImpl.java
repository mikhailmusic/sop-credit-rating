package rut.miit.sopcreditrating.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rut.miit.sopcontracts.dto.request.ClientRequest;
import rut.miit.sopcontracts.dto.request.ClientUpdateRequest;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.exception.BusinessLogicException;
import rut.miit.sopcontracts.exception.CifAlreadyExistsException;
import rut.miit.sopcontracts.exception.EmailAlreadyExistsException;
import rut.miit.sopcontracts.exception.ResourceNotFoundException;
import rut.miit.sopcreditrating.entity.Client;
import rut.miit.sopcreditrating.entity.enums.EmploymentStatus;
import rut.miit.sopcreditrating.repository.ClientRepository;
import rut.miit.sopcreditrating.service.ClientService;
import rut.miit.sopcreditrating.util.EnumUtils;


import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    @Override
    public ClientResponse addClient(ClientRequest clientRequest) {
        clientRepository.findByCif(clientRequest.cif()).ifPresent(c -> {
            throw new CifAlreadyExistsException(clientRequest.cif());
        });

        if (clientRepository.findByEmail(clientRequest.email()).isPresent()) {
            throw new EmailAlreadyExistsException(clientRequest.email());
        }

        EmploymentStatus status =  EnumUtils.parseEnumOrThrow(EmploymentStatus.class, clientRequest.employmentStatus(), "employmentStatus");

        Client client = new Client(clientRequest.cif(), clientRequest.fullName(), clientRequest.birthDate(),
                clientRequest.email(), clientRequest.annualIncome(), clientRequest.totalMonthlyDebtPayment(), status);
        clientRepository.save(client);

        return toDto(client);
    }

    @Override
    public ClientResponse updateClient(UUID id, ClientUpdateRequest clientUpdateRequest) {
        Client client = clientRepository.findById(id)
                .filter(Client::isActive).orElseThrow(() -> new ResourceNotFoundException("Client", id));

        clientRepository.findByEmail(clientUpdateRequest.email()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new EmailAlreadyExistsException(clientUpdateRequest.email());
            }
        });

        EmploymentStatus status = EnumUtils.parseEnumOrThrow(EmploymentStatus.class, clientUpdateRequest.employmentStatus(), "employmentStatus");

        client.setEmail(clientUpdateRequest.email());
        client.setAnnualIncome(clientUpdateRequest.annualIncome());
        client.setTotalMonthlyDebtPayment(clientUpdateRequest.totalMonthlyDebtPayment());
        client.setEmploymentStatus(status);

        clientRepository.save(client);
        return toDto(client);
    }

    @Override
    public ClientResponse getClient(UUID id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return toDto(client);
    }

    @Override
    public ClientResponse getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Client with email=" + email + " not found"));
        return toDto(client);
    }

    @Override
    public ClientResponse getClientByCif(String cif) {
        Client client = clientRepository.findByCif(cif).orElseThrow(() -> new ResourceNotFoundException("Client with CIF=" + cif + " not found"));
        return toDto(client);
    }

    @Override
    public PagedResponse<ClientResponse> getAllClients(int page, int size) {
        boolean active = true;
        return getAllClients(page, size, active);
    }

    @Override
    public PagedResponse<ClientResponse> getAllClients(int page, int size, boolean active) {
        if (page < 0) throw new BusinessLogicException("page must be >= 0");
        if (size <= 0) throw new BusinessLogicException("size must be > 0");

        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Client> clientPage  = clientRepository.findAll(pageable, active);

        return new PagedResponse<>(
                clientPage.getContent().stream().map(this::toDto).toList(),
                clientPage.getNumber(),
                clientPage.getSize(),
                (int) clientPage.getTotalElements(),
                clientPage.getTotalPages(),
                clientPage.isLast()
        );
    }

    @Override
    public void deleteLogicalClient(UUID id) {
        Client client = clientRepository.findById(id)
                .filter(Client::isActive).orElseThrow(() -> new ResourceNotFoundException("Client", id));

        client.setActive(false);
        clientRepository.save(client);
    }


    @Override
    public List<ClientResponse> getByIds(Set<UUID> ids, boolean active) {
        if (ids == null || ids.isEmpty()) return List.of();
        return clientRepository.findByIds(ids, active).stream().map(this::toDto).toList();
    }


    private ClientResponse toDto(Client c) {
        return new ClientResponse(
                c.getId(),
                c.getCif(),
                c.getFullName(),
                c.getBirthDate(),
                c.getEmail(),
                c.getAnnualIncome(),
                c.getTotalMonthlyDebtPayment(),
                c.getEmploymentStatus().name(),
                c.isActive(),
                c.getCreatedDate(),
                c.getUpdatedDate()
        );
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
}
