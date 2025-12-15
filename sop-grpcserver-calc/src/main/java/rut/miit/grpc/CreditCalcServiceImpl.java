package rut.miit.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rut.miit.grpc.sopcreditcalc.service.AssessmentEngine;
import rut.miit.grpc.sopcreditcalc.service.OfferGenerator;

@GrpcService
public class CreditCalcServiceImpl extends CreditCalcServiceGrpc.CreditCalcServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(CreditCalcServiceImpl.class);
    private final AssessmentEngine assessmentEngine;
    private final OfferGenerator offerGenerator;

    public CreditCalcServiceImpl(AssessmentEngine assessmentEngine, OfferGenerator offerGenerator) {
        this.assessmentEngine = assessmentEngine;
        this.offerGenerator = offerGenerator;
    }

    @Override
    public void assess(AssessmentRequest request, StreamObserver<AssessmentResponse> responseObserver) {
        log.info("--> [gRPC] Assess request received: requestId={}, applicationId={}, clientId={}",
                request.getRequestId(), request.getApplicationId(), request.getClientId());
        try {
            AssessmentResponse result = assessmentEngine.assess(request);

            log.info("<-- [gRPC] Assessment response sent: requestId={}, score={}, approved={}, risk={}",
                    result.getRequestId(), result.getCreditScore(), result.getApproved(), result.getRiskLevel());

            responseObserver.onNext(result);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("!!! [gRPC] Invalid assessment request: {}", e.getMessage());
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Invalid request data: " + e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            log.error("!!! [gRPC] Assessment failed for requestId={}: {}", request.getRequestId(), e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Assessment calculation failed: " + e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void generateOffer(OfferRequest request, StreamObserver<OfferResponse> responseObserver) {
        log.info("--> [gRPC] GenerateOffer request received: requestId={}, applicationId={}, score={}, risk={}",
                request.getRequestId(), request.getApplicationId(), request.getCreditScore(), request.getRiskLevel());
        try {
            OfferResponse result = offerGenerator.generateOffer(request);

            log.info("<-- [gRPC] Offer response sent: requestId={}, productId={}, amount={}, apr={}, monthly={}",
                    result.getRequestId(), result.getProductId(), result.getApprovedAmount(),
                    result.getAnnualPercentageRate(), result.getMonthlyPayment());

            responseObserver.onNext(result);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.error("!!! [gRPC] Invalid offer request: {}", e.getMessage());
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Invalid request data: " + e.getMessage()).asRuntimeException()
            );
        } catch (IllegalStateException e) {
            log.error("!!! [gRPC] Cannot generate offer: {}", e.getMessage());
            responseObserver.onError(
                    Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException()
            );
        } catch (Exception e) {
            log.error("!!! [gRPC] Offer generation failed for requestId={}: {}", request.getRequestId(), e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Offer generation failed: " + e.getMessage()).asRuntimeException()
            );
        }
    }
}
