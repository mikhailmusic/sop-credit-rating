package rut.miit.grpc.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

@Component
public class CalculationEventSender {
    private static final Logger log = LoggerFactory.getLogger(CalculationEventSender.class);
    private RabbitTemplate rabbitTemplate;


    public void sendAssessmentCompleted(AssessmentCompletedEvent assessment) {
        log.info("[CALC][ASSESSMENT-RESP] app={} client={} score={} approved={} risk={} reasons={}",
                assessment.applicationId(), assessment.clientId(), assessment.creditScore(), assessment.approved(),
                assessment.riskLevel(), String.join("; ", assessment.rejectionReasons()));

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.FANOUT_RESULTS,
                "",
                assessment
        );
    }

    public void sendOfferGenerated(OfferGeneratedEvent offer) {
        log.info("[CALC][OFFER-CREATED] app={} product={} amount={} term={} apr={} monthly={} expires={}",
                offer.applicationId(), offer.productId(), offer.approvedAmount(), offer.termMonths(),
                offer.annualPercentageRate(), offer.monthlyPayment(), offer.expiresAt());

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.FANOUT_RESULTS,
                "",
                offer
        );
    }


    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
