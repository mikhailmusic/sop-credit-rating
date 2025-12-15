package rut.miit.sopcreditrating.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import rut.miit.sopcontracts.dto.response.ProductResponse;
import rut.miit.sopcontracts.endpoints.OfferController;
import rut.miit.sopcontracts.endpoints.ProductController;
import rut.miit.sopcreditrating.controller.ProductControllerImpl;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProductModelAssembler implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {

    @Override
    public EntityModel<ProductResponse> toModel(ProductResponse product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductControllerImpl.class).getProduct(product.getId())).withSelfRel(),
                linkTo(methodOn(OfferController.class).getOffersByProduct(product.getId())).withRel("offers"),
                linkTo(methodOn(ProductControllerImpl.class).getAllProducts(null)).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<ProductResponse>> toCollectionModel(Iterable<? extends ProductResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(ProductController.class).getAllProducts(null)).withSelfRel());
    }
}
