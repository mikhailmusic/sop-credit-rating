package rut.miit.sopcreditrating.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;

@Component
public class AssessmentEventSender {
    private static final Logger log = LoggerFactory.getLogger(AssessmentEventSender.class);
    private RabbitTemplate rabbitTemplate;

    public void sendAssessmentRequest(AssessmentRequestEvent request) {
        log.info("[MAIN][ASSESSMENT-REQ] app={} client={} amount={} term={} purpose={} income={} debt={} employ={} age={} products={}",
                request.applicationId(), request.clientId(), request.amount(), request.termMonths(),
                request.purpose(), request.annualIncome(), request.totalMonthlyDebtPayment(), request.employmentStatus(),
                request.age(), request.availableProducts() != null ? request.availableProducts().size() : 0
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.RK_ASSESSMENT_REQ,
                request
        );
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
