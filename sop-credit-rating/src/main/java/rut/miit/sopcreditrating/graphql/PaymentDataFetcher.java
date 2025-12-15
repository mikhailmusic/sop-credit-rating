package rut.miit.sopcreditrating.graphql;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.request.PaymentStatusUpdateRequest;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcreditrating.graphql.keys.ClientKey;
import rut.miit.sopcreditrating.graphql.keys.OfferKey;
import rut.miit.sopcreditrating.service.PaymentService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class PaymentDataFetcher {

    private PaymentService paymentService;

    @DgsData(parentType = "Client", field = "payments")
    public CompletableFuture<List<PaymentResponse>> paymentsForClient(DgsDataFetchingEnvironment dfe, @InputArgument("active") Boolean active) {

        ClientResponse client = dfe.getSource();
        boolean flag = active == null || active;

        DataLoader<ClientKey, List<PaymentResponse>> loader = dfe.getDataLoader("paymentsByClientLoader");
        return loader.load(new ClientKey(client.getId(), flag));
    }

    @DgsData(parentType = "Offer", field = "payments")
    public CompletableFuture<List<PaymentResponse>> paymentsForOffer(DgsDataFetchingEnvironment dfe, @InputArgument("active") Boolean active) {

        OfferResponse offer = dfe.getSource();
        boolean flag = (active == null) || active;

        DataLoader<OfferKey, List<PaymentResponse>> loader = dfe.getDataLoader("paymentsByOfferLoader");
        return loader.load(new OfferKey(offer.getId(), flag));
    }



    @DgsQuery
    public PaymentResponse payment(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        return paymentService.getPayment(id);
    }

    @DgsQuery
    public PaymentResponse paymentByReference(@InputArgument("reference") String reference) {
        if (reference == null || reference.isBlank()) throw new DgsBadRequestException("reference is required");
        return paymentService.getByReference(reference);
    }

    @DgsQuery
    public List<PaymentResponse> paymentsByOffer(@InputArgument("offerId") UUID offerId,
                                                 @InputArgument("active") Boolean active) {
        if (offerId == null) throw new DgsBadRequestException("offerId is required");
        boolean flag = active == null || active;
        return paymentService.getByOffer(offerId, flag);
    }

    @DgsQuery
    public List<PaymentResponse> paymentsByClient(@InputArgument("clientId") UUID clientId,
                                                  @InputArgument("active") Boolean active) {
        if (clientId == null) throw new DgsBadRequestException("clientId is required");
        boolean flag = active == null || active;
        return paymentService.getByClient(clientId, flag);
    }

    @DgsQuery
    public PagedResponse<PaymentResponse> payments(@InputArgument("page") Integer page,
                                                   @InputArgument("size") Integer size,
                                                   @InputArgument("active") Boolean active) {
        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;

        boolean flag = (active == null) || active;
        return paymentService.getAllPayments(p, s, flag);
    }


    @DgsMutation
    public PaymentResponse updatePaymentStatus(@InputArgument("id") UUID id,
                                               @InputArgument("input") PaymentStatusUpdateRequest input) {
        if (id == null) throw new DgsBadRequestException("id is required");
        if (input == null) throw new DgsBadRequestException("input is required");
        return paymentService.updateStatus(id, input);
    }


    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
