package rut.miit.sopcreditrating.graphql;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.request.ClientRequest;
import rut.miit.sopcontracts.dto.request.ClientUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcreditrating.service.ClientService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ClientDataFetcher {

    private ClientService clientService;

    @DgsData(parentType = "Application", field = "client")
    public CompletableFuture<ClientResponse> clientForApplication(DgsDataFetchingEnvironment dfe) {
        ApplicationResponse app = dfe.getSource();
        DataLoader<UUID, ClientResponse> loader = dfe.getDataLoader("clientLoader");
        return loader.load(app.getClientId());
    }

    @DgsData(parentType = "Payment", field = "client")
    public CompletableFuture<ClientResponse> clientForPayment(DgsDataFetchingEnvironment dfe) {
        PaymentResponse payment = dfe.getSource();
        DataLoader<UUID, ClientResponse> loader = dfe.getDataLoader("clientLoader");
        return loader.load(payment.getClientId());
    }



    @DgsQuery
    public ClientResponse client(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        return clientService.getClient(id);
    }

    @DgsQuery
    public ClientResponse clientByCif(@InputArgument("cif") String cif) {
        if (cif == null || cif.isBlank()) throw new DgsBadRequestException("cif is required");
        return clientService.getClientByCif(cif);
    }

    @DgsQuery
    public ClientResponse clientByEmail(@InputArgument("email") String email) {
        if (email == null || email.isBlank()) throw new DgsBadRequestException("email is required");
        return clientService.getClientByEmail(email);
    }

    @DgsQuery
    public PagedResponse<ClientResponse> clients(@InputArgument("page") Integer page,
                                        @InputArgument("size") Integer size,
                                        @InputArgument("active") Boolean active) {
        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;
        boolean flag = (active == null) || active;

        return clientService.getAllClients(p, s, flag);
    }


    @DgsMutation
    public ClientResponse addClient(@InputArgument("input") ClientRequest input) {
        if (input == null) throw new DgsBadRequestException("input is required");
        return clientService.addClient(input);
    }

    @DgsMutation
    public ClientResponse updateClient(@InputArgument("id") UUID id,
                                       @InputArgument("input") ClientUpdateRequest input) {
        if (id == null) throw new DgsBadRequestException("id is required");
        if (input == null) throw new DgsBadRequestException("input is required");
        return clientService.updateClient(id, input);
    }

    @DgsMutation
    public UUID deleteClient(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        clientService.deleteLogicalClient(id);
        return id;
    }


    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
