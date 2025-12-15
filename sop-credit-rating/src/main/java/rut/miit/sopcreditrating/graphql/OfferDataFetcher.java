package rut.miit.sopcreditrating.graphql;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.request.OfferDecisionRequest;
import rut.miit.sopcontracts.dto.response.*;
import rut.miit.sopcreditrating.graphql.keys.AppKey;
import rut.miit.sopcreditrating.graphql.keys.ProdKey;
import rut.miit.sopcreditrating.service.OfferService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class OfferDataFetcher {

    private OfferService offerService;


    @DgsData(parentType = "Product", field = "offers")
    public CompletableFuture<List<OfferResponse>> offersForProduct(DgsDataFetchingEnvironment dfe, @InputArgument("active") Boolean active) {
        ProductResponse product = dfe.getSource();
        boolean flag = active == null || active;

        DataLoader<ProdKey, List<OfferResponse>> loader = dfe.getDataLoader("offersByProductLoader");
        return loader.load(new ProdKey(product.getId(), flag));
    }

    @DgsData(parentType = "Application", field = "offers")
    public CompletableFuture<List<OfferResponse>> offersForApplication(DgsDataFetchingEnvironment dfe, @InputArgument("active") Boolean active) {
        ApplicationResponse app = dfe.getSource();
        boolean flag = active == null || active;

        DataLoader<AppKey, List<OfferResponse>> loader = dfe.getDataLoader("offersByApplicationLoader");
        return loader.load(new AppKey(app.getId(), flag));
    }

    @DgsData(parentType = "Payment", field = "offer")
    public CompletableFuture<OfferResponse> offerForPayment(DgsDataFetchingEnvironment dfe) {
        PaymentResponse payment = dfe.getSource();
        DataLoader<UUID, OfferResponse> loader = dfe.getDataLoader("offerLoader");
        return loader.load(payment.getOfferId());
    }



    @DgsQuery
    public OfferResponse offer(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        return offerService.getOffer(id);
    }

    @DgsQuery
    public PagedResponse<OfferResponse> offers(@InputArgument("page") Integer page,
                                      @InputArgument("size") Integer size,
                                      @InputArgument("active") Boolean active) {
        int p = (page == null) ? 0 : page;
        int s = (size == null) ? 10 : size;
        boolean flag = (active == null) || active;

        return offerService.getAllOffers(p, s, flag);
    }

    @DgsQuery
    public List<OfferResponse> offersByApplication(@InputArgument("applicationId") UUID applicationId,
                                                   @InputArgument("active") Boolean active) {
        if (applicationId == null) throw new DgsBadRequestException("applicationId is required");
        boolean flag = (active == null) || active;
        return offerService.getByApplication(applicationId, flag);
    }

    @DgsQuery
    public List<OfferResponse> offersByProduct(@InputArgument("productId") UUID productId,
                                               @InputArgument("active") Boolean active) {
        if (productId == null) throw new DgsBadRequestException("productId is required");
        boolean flag = (active == null) || active;
        return offerService.getByProduct(productId, flag);
    }

    @DgsQuery
    public OfferResponse currentOffer(@InputArgument("applicationId") UUID applicationId) {
        if (applicationId == null) throw new DgsBadRequestException("applicationId is required");
        return offerService.getCurrentByApplication(applicationId);
    }


    @DgsMutation
    public OfferResponse decideOffer(@InputArgument("id") UUID id,
                                     @InputArgument("input") OfferDecisionRequest input) {
        if (id == null) throw new DgsBadRequestException("id is required");
        if (input == null) throw new DgsBadRequestException("input is required");
        return offerService.decideOffer(id, input);
    }

    @DgsMutation
    public UUID cancelAllOffersByApplication(@InputArgument("applicationId") UUID applicationId) {
        if (applicationId == null) throw new DgsBadRequestException("applicationId is required");
        offerService.cancelAllByApplication(applicationId);
        return applicationId;
    }



    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }
}
