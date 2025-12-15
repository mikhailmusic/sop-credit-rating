package rut.miit.sopcreditrating.rabbitmq;

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
import rut.miit.sopcontracts.dto.response.AssessmentCompleted;
import rut.miit.sopcontracts.dto.response.OfferResponse;
import rut.miit.sopcreditrating.entity.enums.OfferStatus;
import rut.miit.sopcreditrating.graphql.event.GraphqlEventPublisher;
import rut.miit.sopcreditrating.service.ApplicationService;
import rut.miit.sopcreditrating.service.OfferService;
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
        value = @Queue(
                name = RabbitMQConstants.QUEUE_MAIN_RESULTS,
                durable = "true",
                arguments = {
                        @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.DLX_EXCHANGE_NAME),
                        @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.RK_DLQ_MAIN_RESULTS)
                }
        ),
        exchange = @Exchange(name = RabbitMQConstants.FANOUT_RESULTS, type = "fanout", durable = "true")
))
public class AssessmentEventListener {
    private static final Logger log = LoggerFactory.getLogger(AssessmentEventListener.class);
    private final Map<Class<?>, Set<UUID>> processedByType = new ConcurrentHashMap<>();
    private ApplicationService applicationService;
    private OfferService offerService;
    private GraphqlEventPublisher eventPublisher;


    @RabbitHandler
    public void handleAssessmentCompleted(@Valid @Payload AssessmentCompletedEvent event, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[MAIN][ASSESSMENT-RESP] Received event: {}", event);

            if (!markProcessed(AssessmentCompletedEvent.class, event.requestId())) {
                log.warn("[MAIN][ASSESSMENT-RESP] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            applicationService.handleAssessmentResult(event);  // Также есть идемпотентные проверки в сервисе
            // GraphQL
            eventPublisher.publishAssessment(convertToDto(event));

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[MAIN][ASSESSMENT-RESP] Failed to process event {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void handleOfferGenerated(@Valid @Payload OfferGeneratedEvent event, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[MAIN][OFFER-CREATED] Received event: {}", event);

            if (!markProcessed(OfferGeneratedEvent.class, event.requestId())) {
                log.warn("[MAIN][OFFER-CREATED] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            offerService.createOffer(event); // Также есть идемпотентные проверки в сервисе
            // GraphQL publish
            eventPublisher.publishOffer(convertToDto(event));

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[MAIN][OFFER-CREATED] Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.DLQ_MAIN_RESULTS, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.DLX_EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_DLQ_MAIN_RESULTS
    ))
    public void handleDlq(Object failedMessage) {
        log.error("!!! [MAIN][DLQ] Received failed message: {}", failedMessage);
    }


    private boolean markProcessed(Class<?> eventType, UUID id) {
        return processedByType.computeIfAbsent(eventType, t -> ConcurrentHashMap.newKeySet()).add(id);
    }

    private AssessmentCompleted convertToDto(AssessmentCompletedEvent assessment) {
        return new AssessmentCompleted(
                assessment.applicationId(), assessment.clientId(), assessment.creditScore(),
                assessment.approved(), assessment.riskLevel().name(), assessment.rejectionReasons()
        );
    }
    private OfferResponse convertToDto(OfferGeneratedEvent offer) {
        return new OfferResponse(
                null, offer.applicationId(), offer.productId(), offer.approvedAmount(),
                offer.termMonths(), offer.annualPercentageRate(), offer.monthlyPayment(),
                offer.expiresAt(), OfferStatus.PROPOSED.name(), true
        );
    }

    @Autowired
    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Autowired
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    @Autowired
    public void setEventPublisher(GraphqlEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
