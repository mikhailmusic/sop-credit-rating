package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcreditrating.graphql.keys.ClientKey;
import rut.miit.sopcreditrating.service.ApplicationService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "applicationsByClientLoader")
public class ApplicationsByClientLoader implements MappedBatchLoader<ClientKey, List<ApplicationResponse>> {

    private ApplicationService applicationService;

    @Override
    public CompletionStage<Map<ClientKey, List<ApplicationResponse>>> load(Set<ClientKey> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Boolean, List<ClientKey>> byActive = keys.stream().collect(Collectors.groupingBy(ClientKey::active));
            Map<ClientKey, List<ApplicationResponse>> result = new HashMap<>();

            for (Map.Entry<Boolean, List<ClientKey>> entry : byActive.entrySet()) {
                boolean active = entry.getKey();
                Set<UUID> clientIds = entry.getValue().stream().map(ClientKey::id).collect(Collectors.toSet());

                List<ApplicationResponse> all = applicationService.getByClientIds(clientIds, active);
                Map<UUID, List<ApplicationResponse>> byClient = all.stream()
                        .collect(Collectors.groupingBy(ApplicationResponse::getClientId));

                for (ClientKey k : entry.getValue()) {
                    result.put(k, byClient.getOrDefault(k.id(), List.of()));
                }
            }

            return result;
        });
    }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
}
