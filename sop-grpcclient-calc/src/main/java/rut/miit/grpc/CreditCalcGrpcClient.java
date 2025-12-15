package rut.miit.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rut.miit.grpc.util.EventProtoMapper;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.util.Optional;

@Component
public class CreditCalcGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(CreditCalcGrpcClient.class);
    private EventProtoMapper mapper;

    @GrpcClient("credit-calc-service")
    private CreditCalcServiceGrpc.CreditCalcServiceBlockingStub creditCalcServiceStub;

    public AssessmentCompletedEvent performAssessment(AssessmentRequestEvent requestEvent) {
        log.info("--> [gRPC-CLIENT] Calling Assess for requestId={}, applicationId={}, clientId={}",
                requestEvent.requestId(), requestEvent.applicationId(), requestEvent.clientId());

        try {
            AssessmentRequest protoRequest = mapper.toGrpcAssessmentRequest(requestEvent);

            AssessmentResponse protoResponse = creditCalcServiceStub.assess(protoRequest);

            AssessmentCompletedEvent assessment = mapper.toAssessmentCompletedEvent(protoResponse);

            log.info("<-- [gRPC-CLIENT] Assessment received: requestId={}, score={}, approved={}, risk={}",
                    assessment.requestId(), assessment.creditScore(), assessment.approved(), assessment.riskLevel());

            return assessment;

        } catch (StatusRuntimeException e) {
            log.error("!!! [gRPC-CLIENT] Assessment failed: status={}, description={}",
                    e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    public Optional<OfferGeneratedEvent> generateOffer(AssessmentCompletedEvent assessment, AssessmentRequestEvent originalRequest) {
        log.info("--> [gRPC-CLIENT] Calling GenerateOffer for requestId={}, applicationId={}",
                assessment.requestId(), assessment.applicationId());

        try {
            OfferRequest protoRequest = mapper.toGrpcOfferRequest(originalRequest, assessment);

            OfferResponse protoResponse = creditCalcServiceStub.generateOffer(protoRequest);

            OfferGeneratedEvent offer = mapper.toOfferGeneratedEvent(protoResponse);

            log.info("<-- [gRPC-CLIENT] Offer received: requestId={}, productId={}, amount={}, apr={}",
                    offer.requestId(), offer.productId(), offer.approvedAmount(), offer.annualPercentageRate());

            return Optional.of(offer);

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.FAILED_PRECONDITION){
                log.warn("[gRPC-CLIENT] Offer not generated due to business rule: status={}, description={}",
                       e.getStatus().getCode(), e.getStatus().getDescription());
                return Optional.empty();
            }

            log.error("!!! [gRPC-CLIENT] Offer generation failed: status={}, description={}",
                   e.getStatus().getCode(), e.getStatus().getDescription());
            throw e;
        }
    }

    @Autowired
    public void setMapper(EventProtoMapper mapper) {
        this.mapper = mapper;
    }
}
