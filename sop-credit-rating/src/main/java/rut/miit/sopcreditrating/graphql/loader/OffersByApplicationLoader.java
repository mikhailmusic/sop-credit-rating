package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcreditrating.graphql.keys.AppKey;
import rut.miit.sopcreditrating.service.OfferService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "offersByApplicationLoader")
public class OffersByApplicationLoader implements MappedBatchLoader<AppKey, List<OfferResponse>> {

    private OfferService offerService;

    @Override
    public CompletionStage<Map<AppKey, List<OfferResponse>>> load(Set<AppKey> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Boolean, List<AppKey>> byActive = keys.stream().collect(Collectors.groupingBy(AppKey::active));
            Map<AppKey, List<OfferResponse>> out = new HashMap<>();

            for (Map.Entry<Boolean, List<AppKey>> entry : byActive.entrySet()) {
                boolean active = entry.getKey();
                Set<UUID> appIds = entry.getValue().stream().map(AppKey::id).collect(Collectors.toSet());

                List<OfferResponse> all = offerService.getByApplicationIds(appIds, active);
                Map<UUID, List<OfferResponse>> byApp = all.stream()
                        .collect(Collectors.groupingBy(OfferResponse::getApplicationId));

                for (AppKey k : entry.getValue()) {
                    out.put(k, byApp.getOrDefault(k.id(), List.of()));
                }
            }
            return out;
        });
    }

    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }
}

