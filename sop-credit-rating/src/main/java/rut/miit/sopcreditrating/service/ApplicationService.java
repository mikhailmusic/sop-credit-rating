package rut.miit.sopcreditrating.service;

import rut.miit.sopcontracts.dto.request.ApplicationRequest;
import rut.miit.sopcontracts.dto.request.ApplicationUpdateRequest;
import rut.miit.sopcontracts.dto.response.ApplicationResponse;
import rut.miit.sopcontracts.dto.response.PagedResponse;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ApplicationService {

    ApplicationResponse createApplication(ApplicationRequest applicationCreationRequest);
    ApplicationResponse updateApplication(UUID id, ApplicationUpdateRequest applicationUpdateRequest);
    void handleAssessmentResult(AssessmentCompletedEvent assessment);

    ApplicationResponse getApplication(UUID id);

    PagedResponse<ApplicationResponse> getAllApplications(int page, int size, String status);
    PagedResponse<ApplicationResponse> getAllApplications(int page, int size, String status, boolean active);

    List<ApplicationResponse> getClientApplications(UUID clientId);
    List<ApplicationResponse> getClientApplications(UUID clientId, boolean active);

    List<ApplicationResponse> getByIds(Set<UUID> ids, boolean active);
    List<ApplicationResponse> getByClientIds(Set<UUID> clientIds, boolean active);

    void deleteLogicalApplication(UUID id);
}
