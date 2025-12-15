package rut.miit.sopcreditrating.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import rut.miit.sopcontracts.dto.response.ClientResponse;
import rut.miit.sopcontracts.dto.response.ClientStatisticsResponse;
import rut.miit.sopcontracts.endpoints.ApplicationController;
import rut.miit.sopcontracts.endpoints.ClientController;
import rut.miit.sopcontracts.endpoints.PaymentController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientModelAssembler implements RepresentationModelAssembler<ClientResponse, EntityModel<ClientResponse>> {

    @Override
    public EntityModel<ClientResponse> toModel(ClientResponse client) {
        return EntityModel.of(client,
                linkTo(methodOn(ClientController.class).getClientById(client.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).getClientStatistics(client.getId())).withRel("statistics"),
                linkTo(methodOn(ApplicationController.class).getApplicationsByClient(client.getId())).withRel("applications"),
                linkTo(methodOn(PaymentController.class).getPaymentsByClient(client.getId())).withRel("payments"),
                linkTo(methodOn(ClientController.class).getAllClients(0, 10)).withRel("collection")
        );
    }

    public EntityModel<ClientStatisticsResponse> toStatisticsModel(ClientStatisticsResponse stats) {
        UUID clientId = stats.getClientId();

        return EntityModel.of(stats,
                linkTo(methodOn(ClientController.class).getClientStatistics(clientId)).withSelfRel(),
                linkTo(methodOn(ClientController.class).getClientById(clientId)).withRel("client"),
                linkTo(methodOn(ApplicationController.class).getApplicationsByClient(clientId)).withRel("applications"),
                linkTo(methodOn(PaymentController.class).getPaymentsByClient(clientId)).withRel("payments")
        );
    }
}