package rut.miit.grpc.rabbitmq;

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
import rut.miit.grpc.CreditCalcGrpcClient;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.assessment.AssessmentRequestEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CalculationEventListener {
    private static final Logger log = LoggerFactory.getLogger(CalculationEventListener.class);
    private final Set<UUID> processedRequests = ConcurrentHashMap.newKeySet();
    private CreditCalcGrpcClient grpcClient;
    private CalculationEventSender eventSender;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.QUEUE_CALC_REQUESTS, durable = "true", arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = RabbitMQConstants.DLX_EXCHANGE_NAME),
                    @Argument(name = "x-dead-letter-routing-key", value = RabbitMQConstants.RK_DLQ_CALC_REQUESTS)
            }),
            exchange = @Exchange(name = RabbitMQConstants.EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_ASSESSMENT_REQ
    ))
    public void receiveAssessmentRequest(@Valid @Payload AssessmentRequestEvent event, Channel channel,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        try {
            log.info("[CALC][ASSESSMENT-REQ] Received event: {}", event);

            if (!processedRequests.add(event.requestId())) {
                log.warn("[CALC][ASSESSMENT-REQ] Duplicate event received for requestId={}", event.requestId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            AssessmentCompletedEvent assessment = grpcClient.performAssessment(event);
            eventSender.sendAssessmentCompleted(assessment);
            Optional<OfferGeneratedEvent> offer = grpcClient.generateOffer(assessment, event);
            offer.ifPresent(offerGeneratedEvent -> eventSender.sendOfferGenerated(offerGeneratedEvent));


            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[CALC][ASSESSMENT-REQ] Failed to process event {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.DLQ_CALC_REQUESTS, durable = "true"),
            exchange = @Exchange(name = RabbitMQConstants.DLX_EXCHANGE_NAME, type = "topic", durable = "true"),
            key = RabbitMQConstants.RK_DLQ_CALC_REQUESTS
    ))
    public void handleDlq(Object failedMessage) {
        log.error("!!! [CALC][DLQ] Received failed message: {}", failedMessage);
    }

    @Autowired
    public void setEventSender(CalculationEventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Autowired
    public void setGrpcClient(CreditCalcGrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }
}
