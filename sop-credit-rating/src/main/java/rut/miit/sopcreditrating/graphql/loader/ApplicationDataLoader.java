package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcreditrating.service.ApplicationService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "applicationLoader")
public class ApplicationDataLoader implements MappedBatchLoader<UUID, ApplicationResponse> {

    private ApplicationService applicationService;

    @Override
    public CompletionStage<Map<UUID, ApplicationResponse>> load(Set<UUID> keys) {
        return CompletableFuture.supplyAsync(() -> {
            List<ApplicationResponse> list = applicationService.getByIds(keys, true);
            return list.stream().collect(Collectors.toMap(ApplicationResponse::getId, it -> it));
        });
    }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
}

