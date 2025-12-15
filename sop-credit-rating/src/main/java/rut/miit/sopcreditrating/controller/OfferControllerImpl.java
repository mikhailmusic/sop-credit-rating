package rut.miit.sopcreditrating.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.OfferDecisionRequest;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.endpoints.OfferController;
import rut.miit.sopcreditrating.assembler.OfferModelAssembler;
import rut.miit.sopcreditrating.service.OfferService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
public class OfferControllerImpl implements OfferController {

    private OfferService offerService;
    private OfferModelAssembler offerModelAssembler;
    private PagedResourcesAssembler<OfferResponse> pagedResourcesAssembler;


    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    @Autowired
    public void setOfferModelAssembler(OfferModelAssembler offerModelAssembler) {
        this.offerModelAssembler = offerModelAssembler;
    }

    @Autowired
    public void setPagedResourcesAssembler(PagedResourcesAssembler<OfferResponse> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    @GetMapping("/{id}")
    public EntityModel<OfferResponse> getOffer(@PathVariable("id") UUID id) {
        OfferResponse offer = offerService.getOffer(id);
        return offerModelAssembler.toModel(offer);
    }

    @Override
    @GetMapping
    public PagedModel<EntityModel<OfferResponse>> getAllOffers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PagedResponse<OfferResponse> pr = offerService.getAllOffers(page, size);

        Page<OfferResponse> springPage = new PageImpl<>(
                pr.content(),
                PageRequest.of(pr.pageNumber(), pr.pageSize()),
                pr.totalElements()
        );

        return pagedResourcesAssembler.toModel(springPage, offerModelAssembler);
    }

    @Override
    @GetMapping("/application/{applicationId}")
    public CollectionModel<EntityModel<OfferResponse>> getOffersByApplication(@PathVariable("applicationId") UUID applicationId) {
        List<OfferResponse> offers = offerService.getByApplication(applicationId);
        return offerModelAssembler.toApplicationOffersModel(applicationId, offers);
    }

    @Override
    @GetMapping("/product/{productId}")
    public CollectionModel<EntityModel<OfferResponse>> getOffersByProduct(@PathVariable("productId") UUID productId) {
        List<OfferResponse> offers = offerService.getByProduct(productId);
        return offerModelAssembler.toProductOffersModel(productId, offers);
    }

    @Override
    @PatchMapping("/{id}/status")
    public EntityModel<OfferResponse> updateOfferStatus(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody OfferDecisionRequest request) {
        OfferResponse updated = offerService.decideOffer(id, request);
        return offerModelAssembler.toModel(updated);
    }
}
