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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rut.miit.sopcontracts.dto.request.ApplicationRequest;
import rut.miit.sopcontracts.dto.request.ApplicationUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopcontracts.endpoints.ApplicationController;
import rut.miit.sopcreditrating.assembler.ApplicationModelAssembler;
import rut.miit.sopcreditrating.service.ApplicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationControllerImpl implements ApplicationController {

    private ApplicationService applicationService;
    private ApplicationModelAssembler applicationModelAssembler;
    private PagedResourcesAssembler<ApplicationResponse> pagedResourcesAssembler;

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Autowired
    public void setApplicationModelAssembler(ApplicationModelAssembler applicationModelAssembler) {
        this.applicationModelAssembler = applicationModelAssembler;
    }

    @Autowired
    public void setPagedResourcesAssembler(PagedResourcesAssembler<ApplicationResponse> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    @PostMapping
    public ResponseEntity<EntityModel<ApplicationResponse>> addApplication(@Valid @RequestBody ApplicationRequest applicationRequest) {
        ApplicationResponse application = applicationService.createApplication(applicationRequest);
        EntityModel<ApplicationResponse> entityModel = applicationModelAssembler.toModel(application);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    @PutMapping("/{id}")
    public EntityModel<ApplicationResponse> updateApplication(@PathVariable("id") UUID id, @Valid @RequestBody ApplicationUpdateRequest applicationUpdateRequest) {
        ApplicationResponse application = applicationService.updateApplication(id, applicationUpdateRequest);
        return applicationModelAssembler.toModel(application);
    }


    @Override
    @GetMapping("/{id}")
    public EntityModel<ApplicationResponse> getApplication(@PathVariable("id") UUID id) {
        ApplicationResponse application = applicationService.getApplication(id);
        return applicationModelAssembler.toModel(application);
    }

    @Override
    @GetMapping
    public PagedModel<EntityModel<ApplicationResponse>> getAllApplications(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        PagedResponse<ApplicationResponse> pagedResponse = applicationService.getAllApplications(page, size, status);

        Page<ApplicationResponse> applicationPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );

        return pagedResourcesAssembler.toModel(applicationPage, applicationModelAssembler);
    }

    @Override
    @GetMapping("/client/{clientId}")
    public CollectionModel<EntityModel<ApplicationResponse>> getApplicationsByClient(@PathVariable("clientId") UUID clientId) {
        List<ApplicationResponse> list = applicationService.getClientApplications(clientId);
        return applicationModelAssembler.toClientApplicationsModel(clientId, list);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable("id") UUID id) {
        applicationService.deleteLogicalApplication(id);
    }
}
