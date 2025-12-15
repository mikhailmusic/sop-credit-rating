package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcreditrating.graphql.keys.ProdKey;
import rut.miit.sopcreditrating.service.OfferService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "offersByProductLoader")
public class OffersByProductLoader implements MappedBatchLoader<ProdKey, List<OfferResponse>> {

    private OfferService offerService;

    @Override
    public CompletionStage<Map<ProdKey, List<OfferResponse>>> load(Set<ProdKey> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Boolean, List<ProdKey>> byActive = keys.stream().collect(Collectors.groupingBy(ProdKey::active));
            Map<ProdKey, List<OfferResponse>> out = new HashMap<>();

            for (Map.Entry<Boolean, List<ProdKey>> entry : byActive.entrySet()) {
                boolean active = entry.getKey();
                Set<UUID> prodIds = entry.getValue().stream().map(ProdKey::id).collect(Collectors.toSet());

                List<OfferResponse> all = offerService.getByProductIds(prodIds, active);
                Map<UUID, List<OfferResponse>> byProd = all.stream()
                        .collect(Collectors.groupingBy(OfferResponse::getProductId));

                for (ProdKey k : entry.getValue()) {
                    out.put(k, byProd.getOrDefault(k.id(), List.of()));
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
