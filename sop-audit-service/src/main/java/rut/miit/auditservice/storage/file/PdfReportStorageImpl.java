package rut.miit.auditservice.storage.file;

import org.springframework.stereotype.Component;
import rut.miit.auditservice.config.PdfProperties;

@Component
class PdfReportStorageImpl extends BaseFileStorage implements PdfReportStorage {

    public PdfReportStorageImpl(PdfProperties properties) {
        super(properties.getBaseDir(), properties.getFileExtension());
    }
}
