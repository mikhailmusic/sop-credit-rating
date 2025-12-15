package rut.miit.auditservice.storage.csv;

import org.springframework.stereotype.Component;
import rut.miit.auditservice.config.CsvProperties;
import rut.miit.auditservice.model.AssessmentResponse;

@Component
class CsvAssessmentResponseStorage extends CsvGenericAuditStorage<AssessmentResponse> implements AssessmentResponseStorage {

    public CsvAssessmentResponseStorage(CsvProperties props) {
        super(AssessmentResponse.class, props.getBaseDir(), props.getMaxFileSizeMb());
    }
}
