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
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = RabbitMQConstants.QUEUE_AUDIT_RESULTS, durable = "true", arguments = {
                @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.DLX_EXCHANGE_NAME),
                @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.RK_DLQ_AUDIT_RESULTS)
        }
        ),
        exchange = @Exchange(name = RabbitMQConstants.FANOUT_RESULTS, type = "fanout", durable = "true")
))
public class AuditResultsListener {
    private static final Logger log = LoggerFactory.getLogger(AuditResultsListener.class);
    private final Map<Class<?>, Set<UUID>> processedByType = new ConcurrentHashMap<>();

    private AuditService auditService;

    @RabbitHandler
    public void handleAssessmentCompleted(@Valid @Payload AssessmentCompletedEvent event, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("[AUDIT][ASSESSMENT-RESP] Received event: {}", event);

            if (!markProcessed(AssessmentCompletedEvent.class, event.requestId()) || auditService.assessmentResponseExists(event.requestId())) {
                log.warn("[AUDIT][ASSESSMENT-RESP] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            auditService.saveAssessmentResponse(event);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[AUDIT][ASSESSMENT-RESP] Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void handleOfferGenerated(@Valid @Payload OfferGeneratedEvent event, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("[AUDIT][OFFER-CREATED] Received event: {}", event);

            if (!markProcessed(OfferGeneratedEvent.class, event.requestId()) || auditService.offerExists(event.requestId())) {
                log.warn("[AUDIT][OFFER-CREATED] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            auditService.saveOffer(event);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[AUDIT][OFFER-CREATED] Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.DLQ_AUDIT_RESULTS, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.DLX_EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_DLQ_AUDIT_RESULTS
    ))
    public void handleDlq(Object failedMessage) {
        log.error("!!! [AUDIT][DLQ-RESULTS] Received failed message: {}", failedMessage);
    }

    private boolean markProcessed(Class<?> eventType, UUID requestId) {
        return processedByType.computeIfAbsent(eventType, t -> ConcurrentHashMap.newKeySet()).add(requestId);
    }

    @Autowired
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }
}