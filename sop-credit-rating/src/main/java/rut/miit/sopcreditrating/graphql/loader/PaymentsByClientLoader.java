package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcreditrating.graphql.keys.ClientKey;
import rut.miit.sopcreditrating.service.PaymentService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "paymentsByClientLoader")
public class PaymentsByClientLoader implements MappedBatchLoader<ClientKey, List<PaymentResponse>> {

    private PaymentService paymentService;

    @Override
    public CompletionStage<Map<ClientKey, List<PaymentResponse>>> load(Set<ClientKey> keys) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Boolean, List<ClientKey>> byActive = keys.stream().collect(Collectors.groupingBy(ClientKey::active));
            Map<ClientKey, List<PaymentResponse>> out = new HashMap<>();

            for (Map.Entry<Boolean, List<ClientKey>> entry : byActive.entrySet()) {
                boolean active = entry.getKey();
                Set<UUID> clientIds = entry.getValue().stream().map(ClientKey::id).collect(Collectors.toSet());

                List<PaymentResponse> all = paymentService.getByClientIds(clientIds, active);
                Map<UUID, List<PaymentResponse>> byClient = all.stream()
                        .collect(Collectors.groupingBy(PaymentResponse::getClientId));

                for (ClientKey k : entry.getValue()) {
                    out.put(k, byClient.getOrDefault(k.id(), List.of()));
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

