package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcreditrating.graphql.keys.OfferKey;
import rut.miit.sopcreditrating.service.PaymentService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "paymentsByOfferLoader")
public class PaymentsByOfferLoader implements MappedBatchLoader<OfferKey, List<PaymentResponse>> {

    private PaymentService paymentService;

    @Override
    public CompletionStage<Map<OfferKey, List<PaymentResponse>>> load(Set<OfferKey> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Boolean, List<OfferKey>> byActive = keys.stream().collect(Collectors.groupingBy(OfferKey::active));
            Map<OfferKey, List<PaymentResponse>> out = new HashMap<>();

            for (Map.Entry<Boolean, List<OfferKey>> entry : byActive.entrySet()) {
                boolean active = entry.getKey();
                Set<UUID> offerIds = entry.getValue().stream().map(OfferKey::id).collect(Collectors.toSet());

                List<PaymentResponse> all = paymentService.getByOfferIds(offerIds, active);
                Map<UUID, List<PaymentResponse>> byOffer = all.stream()
                        .collect(Collectors.groupingBy(PaymentResponse::getOfferId));

                for (OfferKey k : entry.getValue()) {
                    out.put(k, byOffer.getOrDefault(k.id(), List.of()));
                }
            }
            return out;
        });
    }

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}


