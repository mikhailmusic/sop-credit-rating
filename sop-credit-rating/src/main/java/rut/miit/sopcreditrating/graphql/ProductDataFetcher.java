package rut.miit.sopcreditrating.graphql;

import com.netflix.graphql.dgs.*;
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.request.ProductRequest;
import rut.miit.sopcontracts.dto.request.ProductUpdateRequest;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcreditrating.service.ProductService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@DgsComponent
public class ProductDataFetcher {

    private ProductService productService;

    @DgsData(parentType = "Offer", field = "product")
    public CompletableFuture<ProductResponse> productForOffer(DgsDataFetchingEnvironment dfe) {
        OfferResponse offer = dfe.getSource();
        DataLoader<UUID, ProductResponse> loader = dfe.getDataLoader("productLoader");
        return loader.load(offer.getProductId());
    }



    @DgsQuery
    public ProductResponse product(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("Product id is required");
        return productService.getProduct(id);
    }

    @DgsQuery
    public ProductResponse productByCode(@InputArgument("code") String code) {
        if (code == null || code.isBlank()) throw new DgsBadRequestException("Product code is required");
        return productService.getByCode(code);
    }

    @DgsQuery
    public List<ProductResponse> products(@InputArgument("purpose") String purpose,
                                          @InputArgument("active") Boolean active) {
        boolean flag = active == null || active;
        if (purpose == null || purpose.isBlank()) {
            return productService.getAllProducts(flag);
        }
        return productService.getByPurpose(normalizeEnum(purpose), flag);
    }

    @DgsMutation
    public ProductResponse createProduct(@InputArgument("input") ProductRequest input) {
        if (input == null) throw new DgsBadRequestException("input is required");
        return productService.createProduct(input);
    }

    @DgsMutation
    public ProductResponse updateProduct(@InputArgument("id") UUID id,
                                         @InputArgument("input") ProductUpdateRequest input) {
        if (id == null) throw new DgsBadRequestException("id is required");
        if (input == null) throw new DgsBadRequestException("input is required");
        return productService.updateProduct(id, input);
    }

    @DgsMutation
    public UUID deleteProduct(@InputArgument("id") UUID id) {
        if (id == null) throw new DgsBadRequestException("id is required");
        productService.deleteLogicalProduct(id);
        return id;
    }

    private static String normalizeEnum(String raw) {
        return raw == null ? null : raw.trim().toUpperCase(Locale.ROOT);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

}
