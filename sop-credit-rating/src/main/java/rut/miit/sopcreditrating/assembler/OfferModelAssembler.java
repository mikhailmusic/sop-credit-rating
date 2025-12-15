package rut.miit.sopcreditrating.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.endpoints.ApplicationController;
import rut.miit.sopcontracts.endpoints.OfferController;
import rut.miit.sopcontracts.endpoints.PaymentController;
import rut.miit.sopcontracts.endpoints.ProductController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OfferModelAssembler implements RepresentationModelAssembler<OfferResponse, EntityModel<OfferResponse>> {

    @Override
    public EntityModel<OfferResponse> toModel(OfferResponse offer) {

        return EntityModel.of(offer,
                linkTo(methodOn(OfferController.class).getOffer(offer.getId())).withSelfRel(),
                linkTo(methodOn(OfferController.class).getAllOffers(0, 10)).withRel("collection"),

                // связанные сущности:
                linkTo(methodOn(ApplicationController.class).getApplication(offer.getApplicationId())).withRel("application"),
                linkTo(methodOn(ProductController.class).getProduct(offer.getProductId())).withRel("product"),
                linkTo(methodOn(PaymentController.class).getPaymentsByOffer(offer.getId())).withRel("payments")
        );
    }

    public CollectionModel<EntityModel<OfferResponse>> toApplicationOffersModel(UUID applicationId, Iterable<? extends OfferResponse> offers) {
        CollectionModel<EntityModel<OfferResponse>> collection = RepresentationModelAssembler.super.toCollectionModel(offers);
        collection.add(linkTo(methodOn(OfferController.class).getOffersByApplication(applicationId)).withSelfRel());
        collection.add(linkTo(methodOn(ApplicationController.class).getApplication(applicationId)).withRel("application"));
        return collection;
    }

    public CollectionModel<EntityModel<OfferResponse>> toProductOffersModel(UUID productId, Iterable<? extends OfferResponse> offers) {
        CollectionModel<EntityModel<OfferResponse>> collection = RepresentationModelAssembler.super.toCollectionModel(offers);
        collection.add(linkTo(methodOn(OfferController.class).getOffersByProduct(productId)).withSelfRel());
        collection.add(linkTo(methodOn(ProductController.class).getProduct(productId)).withRel("product"));
        return collection;
    }
}
