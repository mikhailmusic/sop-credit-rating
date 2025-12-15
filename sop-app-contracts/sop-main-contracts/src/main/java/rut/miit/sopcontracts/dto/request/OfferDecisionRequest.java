package rut.miit.sopcontracts.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Accept or decline an offer")
public record OfferDecisionRequest(

        @Schema(description = "Decision on the offer", example = "ACCEPTED", allowableValues = {"ACCEPTED", "REJECTED"})
        @NotBlank(message = "Decision is required and cannot be blank")
        @Pattern(regexp = "^(ACCEPTED|REJECTED)$", message = "Decision must be either ACCEPTED or REJECTED")
        String status
) {}
