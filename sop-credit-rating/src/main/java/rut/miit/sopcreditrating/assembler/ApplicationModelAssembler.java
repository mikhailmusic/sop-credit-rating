package rut.miit.sopcreditrating.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.endpoints.ApplicationController;
import rut.miit.sopcontracts.endpoints.ClientController;
import rut.miit.sopcontracts.endpoints.OfferController;
import rut.miit.sopcontracts.endpoints.PaymentController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApplicationModelAssembler implements RepresentationModelAssembler<ApplicationResponse, EntityModel<ApplicationResponse>> {

    @Override
    public EntityModel<ApplicationResponse> toModel(ApplicationResponse app) {
        return EntityModel.of(app,
                linkTo(methodOn(ApplicationController.class).getApplication(app.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).getClientById(app.getClientId())).withRel("client"),
                linkTo(methodOn(OfferController.class).getOffersByApplication(app.getId())).withRel("offers"),
                linkTo(methodOn(ApplicationController.class).getAllApplications(0, 10, null)).withRel("collection")
        );
    }

    public CollectionModel<EntityModel<ApplicationResponse>> toClientApplicationsModel(UUID clientId, Iterable<? extends ApplicationResponse> applications) {
        CollectionModel<EntityModel<ApplicationResponse>> collection = RepresentationModelAssembler.super.toCollectionModel(applications);
        collection.add(linkTo(methodOn(ApplicationController.class).getApplicationsByClient(clientId)).withSelfRel());
        collection.add(linkTo(methodOn(ClientController.class).getClientById(clientId)).withRel("client"));
        return collection;
    }

}

