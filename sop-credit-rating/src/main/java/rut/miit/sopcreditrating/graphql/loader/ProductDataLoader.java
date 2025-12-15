package rut.miit.sopcreditrating.graphql.loader;

import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcreditrating.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@DgsDataLoader(name = "productLoader")
public class ProductDataLoader implements MappedBatchLoader<UUID, ProductResponse> {

    private ProductService productService;

    @Override
    public CompletionStage<Map<UUID, ProductResponse>> load(Set<UUID> keys) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductResponse> list = productService.getByIds(keys, true);
            return list.stream().collect(Collectors.toMap(ProductResponse::getId, it -> it));
        });
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
