package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcreditrating.service.ClientService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "clientLoader")
public class ClientDataLoader implements MappedBatchLoader<UUID, ClientResponse> {

    private ClientService clientService;

    @Override
    public CompletionStage<Map<UUID, ClientResponse>> load(Set<UUID> keys) {
        return CompletableFuture.supplyAsync(() -> {
            List<ClientResponse> list = clientService.getByIds(keys, true);
            return list.stream().collect(Collectors.toMap(ClientResponse::getId, it -> it));
        });
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}

