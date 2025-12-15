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
import rut.miit.sopcontracts.dto.request.PaymentStatusUpdateRequest;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.dto.response.PaymentResponse;
import rut.miit.sopcontracts.endpoints.PaymentController;
import rut.miit.sopcreditrating.assembler.PaymentModelAssembler;
import rut.miit.sopcreditrating.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentControllerImpl implements PaymentController {

    private PaymentService paymentService;
    private PaymentModelAssembler paymentModelAssembler;
    private PagedResourcesAssembler<PaymentResponse> pagedResourcesAssembler;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    public void setPaymentModelAssembler(PaymentModelAssembler paymentModelAssembler) {
        this.paymentModelAssembler = paymentModelAssembler;
    }

    @Autowired
    public void setPagedResourcesAssembler(PagedResourcesAssembler<PaymentResponse> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    @GetMapping("/{id}")
    public EntityModel<PaymentResponse> getPayment(@PathVariable("id") UUID id) {
        PaymentResponse payment = paymentService.getPayment(id);
        return paymentModelAssembler.toModel(payment);
    }

    @Override
    @GetMapping("/by-reference/{reference}")
    public EntityModel<PaymentResponse> getPaymentByReference(@PathVariable("reference") String reference) {
        PaymentResponse payment = paymentService.getByReference(reference);
        return paymentModelAssembler.toModel(payment);
    }


    @Override
    @GetMapping("/offer/{offerId}")
    public CollectionModel<EntityModel<PaymentResponse>> getPaymentsByOffer(@PathVariable("offerId") UUID offerId) {
        List<PaymentResponse> list = paymentService.getByOffer(offerId);
        return paymentModelAssembler.toOfferPaymentsModel(offerId, list);
    }

    @Override
    @PatchMapping("/{id}/status")
    public EntityModel<PaymentResponse> updatePaymentStatus(@PathVariable("id") UUID id,
                                                            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        PaymentResponse updated = paymentService.updateStatus(id, request);
        return paymentModelAssembler.toModel(updated);
    }

    @Override
    @GetMapping("/client/{clientId}")
    public CollectionModel<EntityModel<PaymentResponse>> getPaymentsByClient(@PathVariable("clientId") UUID clientId) {
        List<PaymentResponse> list = paymentService.getByClient(clientId);
        return paymentModelAssembler.toClientPaymentsModel(clientId, list);
    }

    @Override
    @GetMapping
    public PagedModel<EntityModel<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        PagedResponse<PaymentResponse> pagedResponse = paymentService.getAllPayments(page, size);
        Page<PaymentResponse> springPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedResourcesAssembler.toModel(springPage, paymentModelAssembler);
    }
}
