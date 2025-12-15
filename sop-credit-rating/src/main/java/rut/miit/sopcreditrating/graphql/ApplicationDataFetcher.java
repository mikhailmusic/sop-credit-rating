package rut.miit.sopcreditrating.graphql;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.request.ApplicationRequest;
import rut.miit.sopcontracts.dto.request.ApplicationUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcreditrating.graphql.keys.ClientKey;
import rut.miit.sopcreditrating.service.ApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class ApplicationDataFetcher {

    private ApplicationService applicationService;

    @DgsData(parentType = "Client", field = "applications")
    public CompletableFuture<List<ApplicationResponse>> applicationsForClient(DgsDataFetchingEnvironment dfe, @InputArgument("active") Boolean active) {
        ClientResponse client = dfe.getSource();
        boolean flag = active == null || active;

        DataLoader<ClientKey, List<ApplicationResponse>> loader = dfe.getDataLoader("applicationsByClientLoader");
        return loader.load(new ClientKey(client.getId(), flag));
    }


    @DgsData(parentType = "Offer", field = "application")
    public CompletableFuture<ApplicationResponse> applicationForOffer(DgsDataFetchingEnvironment dfe) {
        OfferResponse offer = dfe.getSource();
        DataLoader<UUID, ApplicationResponse> loader = dfe.getDataLoader("applicationLoader");
        return loader.load(offer.getApplicationId());
    }



    @DgsQuery
    public ApplicationResponse application(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        return applicationService.getApplication(id);
    }

    @DgsQuery
    public PagedResponse<ApplicationResponse> applications(@InputArgument("clientId") UUID clientId,
                                                  @InputArgument("status") String status,
                                                  @InputArgument("page") Integer page,
                                                  @InputArgument("size") Integer size,
                                                  @InputArgument("active") Boolean active) {
        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;
        if (p < 0) throw new DgsBadRequestException("page must be >= 0");
        if (s <= 0) throw new DgsBadRequestException("size must be > 0");
        boolean flag = (active == null) || active;

        if (clientId == null) {
            return applicationService.getAllApplications(p, s, status, flag);
        }

        List<ApplicationResponse> all = applicationService.getClientApplications(clientId, flag);

        if (status != null && !status.isBlank()) {
            String normalized = status.trim().toUpperCase();
            all = all.stream().filter(a -> normalized.equals(a.getApplicationStatus())).toList();
        }

        int totalElements = all.size();
        int from = Math.min(p * s, totalElements);
        int to   = Math.min(from + s, totalElements);

        List<ApplicationResponse> pageContent = (from < to) ? all.subList(from, to) : List.of();
        int totalPages = (int) Math.ceil(totalElements / (double) s);
        boolean last = totalPages == 0 || p >= totalPages - 1;

        return new PagedResponse<>(pageContent, p, s, totalElements, totalPages, last);
    }


    @DgsMutation
    public ApplicationResponse addApplication(@InputArgument("input") ApplicationRequest input) {
        if (input == null) throw new DgsBadRequestException("input is required");
        return applicationService.createApplication(input);
    }

    @DgsMutation
    public ApplicationResponse updateApplication(@InputArgument("id") UUID id,
                                                 @InputArgument("input") ApplicationUpdateRequest input) {
        if (id == null) throw new DgsBadRequestException("id is required");
        if (input == null) throw new DgsBadRequestException("input is required");
        return applicationService.updateApplication(id, input);
    }

    @DgsMutation
    public UUID deleteApplication(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        applicationService.deleteLogicalApplication(id);
        return id;
    }


    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
}
