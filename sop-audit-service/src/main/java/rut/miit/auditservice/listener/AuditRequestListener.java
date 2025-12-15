package rut.miit.auditservice.listener;

import com.rabbitmq.client.Channel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rut.miit.auditservice.service.AuditService;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuditRequestListener {
    private static final Logger log = LoggerFactory.getLogger(AuditRequestListener.class);
    private final Set<UUID> processedRequests = ConcurrentHashMap.newKeySet();

    private AuditService auditService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.QUEUE_AUDIT_REQUESTS, durable = "true", arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.DLX_EXCHANGE_NAME),
                    @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.RK_DLQ_AUDIT_REQUESTS)
                    }
            ),
            exchange = @Exchange(name = RabbitMQConstants.EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_ASSESSMENT_REQ
    ))
    public void handleAssessmentRequest(@Valid @Payload AssessmentRequestEvent event, Channel channel,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[AUDIT][ASSESSMENT-REQ] Received event: {}", event);

            if (!processedRequests.add(event.requestId()) || auditService.assessmentRequestExists(event.requestId())) {
                log.warn("[AUDIT][ASSESSMENT-REQ] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            auditService.saveAssessmentRequest(event);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[AUDIT][ASSESSMENT-REQ] Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.DLQ_AUDIT_REQUESTS, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.DLX_EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_DLQ_AUDIT_REQUESTS
    ))
    public void handleDlq(Object failedMessage) {
        log.error("!!! [AUDIT][DLQ-REQUESTS] Received failed message: {}", failedMessage);
    }

    @Autowired
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }
}
