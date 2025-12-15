package rut.miit.notificationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;

@Configuration
public class RabbitMQConfig {


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
}