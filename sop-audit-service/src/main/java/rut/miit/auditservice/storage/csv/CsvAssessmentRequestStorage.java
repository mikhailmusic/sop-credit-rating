package rut.miit.auditservice.storage.csv;

import org.springframework.stereotype.Component;
import rut.miit.auditservice.config.CsvProperties;
import rut.miit.auditservice.model.AssessmentRequest;

@Component
class CsvAssessmentRequestStorage extends CsvGenericAuditStorage<AssessmentRequest> implements AssessmentRequestStorage {

    public CsvAssessmentRequestStorage(CsvProperties props) {
        super(AssessmentRequest.class, props.getBaseDir(), props.getMaxFileSizeMb());
    }
}