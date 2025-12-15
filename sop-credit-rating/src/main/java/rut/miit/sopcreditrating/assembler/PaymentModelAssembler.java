package rut.miit.sopcreditrating.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcontracts.endpoints.ClientController;
import rut.miit.sopcontracts.endpoints.OfferController;
import rut.miit.sopcontracts.endpoints.PaymentController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<PaymentResponse, EntityModel<PaymentResponse>> {

    @Override
    public EntityModel<PaymentResponse> toModel(PaymentResponse payment) {
        return EntityModel.of(
                payment,
                linkTo(methodOn(PaymentController.class).getPayment(payment.getId())).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments(0, 10)).withRel("collection"),
                linkTo(methodOn(PaymentController.class).getPaymentsByOffer(payment.getOfferId())).withRel("by-offer"),
                linkTo(methodOn(OfferController.class).getOffer(payment.getOfferId())).withRel("offer"),
                linkTo(methodOn(ClientController.class).getClientById(payment.getClientId())).withRel("client")
        );
    }

    public CollectionModel<EntityModel<PaymentResponse>> toOfferPaymentsModel(UUID offerId, Iterable<? extends PaymentResponse> payments) {
        CollectionModel<EntityModel<PaymentResponse>> collection = RepresentationModelAssembler.super.toCollectionModel(payments);
        collection.add(linkTo(methodOn(PaymentController.class).getPaymentsByOffer(offerId)).withSelfRel());
        collection.add(linkTo(methodOn(OfferController.class).getOffer(offerId)).withRel("offer"));
        return collection;
    }

    public CollectionModel<EntityModel<PaymentResponse>> toClientPaymentsModel(UUID clientId, Iterable<? extends PaymentResponse> payments) {
        CollectionModel<EntityModel<PaymentResponse>> collection = RepresentationModelAssembler.super.toCollectionModel(payments);
        collection.add(linkTo(methodOn(PaymentController.class).getPaymentsByClient(clientId)).withSelfRel());
        collection.add(linkTo(methodOn(ClientController.class).getClientById(clientId)).withRel("client"));
        return collection;
    }
}