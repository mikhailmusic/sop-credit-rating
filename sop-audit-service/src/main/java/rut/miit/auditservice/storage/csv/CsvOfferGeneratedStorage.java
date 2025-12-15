package rut.miit.auditservice.storage.csv;

import org.springframework.stereotype.Component;
import rut.miit.auditservice.config.CsvProperties;
import rut.miit.auditservice.model.OfferGenerated;

@Component
class CsvOfferGeneratedStorage extends CsvGenericAuditStorage<OfferGenerated> implements OfferGeneratedStorage {

    public CsvOfferGeneratedStorage(CsvProperties props) {
        super(OfferGenerated.class, props.getBaseDir(), props.getMaxFileSizeMb());
    }
}