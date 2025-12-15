package rut.miit.grpc.rabbitmq;

import com.rabbitmq.client.Channel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rut.miit.sopeventcontracts.RabbitMQConstants;
import rut.miit.sopeventcontracts.assessment.AssessmentCompletedEvent;
import rut.miit.sopeventcontracts.offer.OfferGeneratedEvent;

import java.io.IOException;


@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = RabbitMQConstants.QUEUE_CALC_INTERNAL, durable = "true"),
        exchange = @Exchange(name = RabbitMQConstants.FANOUT_RESULTS, type = "fanout", durable = "true")
))
public class InternalCheckListener {
    private static final Logger log = LoggerFactory.getLogger(InternalCheckListener.class);

    @RabbitHandler
    public void handleAssessmentCompleted(@Valid @Payload AssessmentCompletedEvent event, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("[CALC-INTERNAL] Assessment completed broadcasted: app={} score={} approved={}",
                    event.applicationId(), event.creditScore(), event.approved());

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[CALC-INTERNAL] Failed to log assessment: {}", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void handleOfferGenerated(@Valid @Payload OfferGeneratedEvent event, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("[CALC-INTERNAL] Offer generated broadcasted: app={} amount={} apr={}",
                    event.applicationId(), event.approvedAmount(), event.annualPercentageRate());

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("[CALC-INTERNAL] Failed to log offer: {}", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

}