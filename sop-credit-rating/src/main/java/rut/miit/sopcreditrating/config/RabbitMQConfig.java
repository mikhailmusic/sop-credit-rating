package rut.miit.sopcreditrating.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import rut.miit.sopeventcontracts.RabbitMQConstants;


@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange creditExchange() {
        // durable=true - обменник переживет перезагрузку брокера
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME, true, false);
    }

    @Bean
    public FanoutExchange resultsFanout() {
        return new FanoutExchange(RabbitMQConstants.FANOUT_RESULTS, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    @Bean
    public RabbitListenerConfigurer rabbitListenerConfigurer(Validator validator) {
        return new RabbitListenerConfigurer() {
            @Override
            public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
                registrar.setValidator(validator);
            }
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);

        // Устанавливаем callback для publisher confirms
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.err.println("NACK: Message delivery failed! " + cause);
            }
        });

        return rabbitTemplate;
    }
}
