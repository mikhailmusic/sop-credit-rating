package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcreditrating.service.OfferService;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "offerLoader")
public class OfferDataLoader implements MappedBatchLoader<UUID, OfferResponse> {

    private OfferService offerService;

    @Override
    public CompletionStage<Map<UUID, OfferResponse>> load(Set<UUID> keys) {
        return CompletableFuture.supplyAsync(() -> {
            List<OfferResponse> list = offerService.getByIds(keys, true);
            return list.stream().collect(Collectors.toMap(OfferResponse::getId, it -> it));
        });
    }

    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }
}
