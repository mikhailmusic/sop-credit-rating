package rut.miit.sopcreditrating.controller;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rut.miit.sopcontracts.endpoints.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootControllerImpl implements RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> root = new RepresentationModel<>();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        root.add(
                linkTo(methodOn(RootControllerImpl.class).getRoot()).withSelfRel(),
                linkTo(methodOn(ClientController.class).getAllClients(0, 10)).withRel("clients"),
                linkTo(methodOn(ApplicationController.class).getAllApplications(0, 10, null)).withRel("applications"),
                linkTo(methodOn(OfferController.class).getAllOffers(0, 10)).withRel("offers"),
                linkTo(methodOn(PaymentController.class).getAllPayments(0, 10)).withRel("payments"),
                linkTo(methodOn(ProductController.class).getAllProducts(null)).withRel("products"),

                Link.of(baseUrl + "/swagger-ui/index.html").withRel("documentation")
        );

        return root;
    }
}
