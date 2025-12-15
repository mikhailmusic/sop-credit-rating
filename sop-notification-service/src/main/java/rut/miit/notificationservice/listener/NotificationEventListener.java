package rut.miit.notificationservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import rut.miit.notificationservice.websocket.NotificationHandler;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = RabbitMQConstants.QUEUE_NOTIFICATION_RESULTS, durable = "true", arguments = {
                @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.DLX_EXCHANGE_NAME),
                @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.RK_DLQ_NOTIFICATION_RESULTS)
        }
        ),
        exchange = @Exchange(name = RabbitMQConstants.FANOUT_RESULTS, type = "fanout", durable = "true")
))
public class NotificationEventListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private final Map<Class<?>, Set<UUID>> processedByType = new ConcurrentHashMap<>();
    private NotificationHandler notificationHandler;
    private ObjectMapper objectMapper;

    @RabbitHandler
    public void handleAssessmentResponse(@Valid @Payload AssessmentCompletedEvent event, Channel channel,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[NOTIFICATION][ASSESSMENT-RESP] Received event: {}", event);

            if (!markProcessed(AssessmentCompletedEvent.class, event.requestId())) {
                log.warn("[NOTIFICATION][ASSESSMENT-RESP] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            String message = buildMessage("ASSESSMENT_COMPLETED", event);
            notificationHandler.broadcast("ASSESSMENT_COMPLETED", message);

            log.info("Assessment notification sent to WebSocket clients");

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[NOTIFICATION][ASSESSMENT-RESP] Failed to process event {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void handleOfferCreated(@Valid @Payload OfferGeneratedEvent event, Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[NOTIFICATION][OFFER-CREATED] Received event: {}", event);

            if (!markProcessed(OfferGeneratedEvent.class, event.requestId())) {
                log.warn("[NOTIFICATION][OFFER-CREATED] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            String message = buildMessage("OFFER_GENERATED", event);
            notificationHandler.broadcast("OFFER_GENERATED", message);

            log.info("Offer notification sent to WebSocket clients");

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[NOTIFICATION][OFFER-CREATED] Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.DLQ_NOTIFICATION_RESULTS, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.DLX_EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_DLQ_NOTIFICATION_RESULTS
    ))
    public void handleDlq(Object failedMessage) {
        log.error("!!! [NOTIFICATION][DLQ] Received failed message: {}", failedMessage);
    }

    private boolean markProcessed(Class<?> eventType, UUID id) {
        return processedByType.computeIfAbsent(eventType, t -> ConcurrentHashMap.newKeySet()).add(id);
    }

    private String buildMessage(String eventType, Object payload) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", eventType);
            message.put("timestamp", OffsetDateTime.now());
            message.put("data", payload);

            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification message", e);
            return String.format("{\"type\":\"%s\",\"error\":\"Serialization failed\"}", eventType);
        }
    }

    @Autowired
    public void setNotificationHandler(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
