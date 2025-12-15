package rut.miit.sopcontracts.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AssessmentCompleted(

        UUID applicationId,
        UUID clientId,
        BigDecimal creditScore,
        boolean approved,
        String riskLevel,
        List<String> rejectionReasons
) {}