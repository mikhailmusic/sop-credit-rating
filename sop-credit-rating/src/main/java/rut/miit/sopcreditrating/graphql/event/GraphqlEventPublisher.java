package rut.miit.sopcreditrating.graphql.event;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import rut.miit.sopcontracts.dto.response.AssessmentCompleted;
import rut.miit.sopcontracts.dto.response.OfferResponse;


@Component
public class GraphqlEventPublisher {
    // Reactive streams
    private final Sinks.Many<AssessmentCompleted> assessmentSink;
    private final Sinks.Many<OfferResponse> offerSink;

    // Когда последний подписчик отключается от Flux:
    // autoCancel(true) - автоматически отменяет upstream (источник данных), освобождает ресурсы (по умолчанию)
    // autoCancel(false) - Sink будет работать вечно, даже без подписчиков
    public GraphqlEventPublisher() {
        this.assessmentSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
        this.offerSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }

    // методы для отправки событий (из RabbitListener)
    public void publishAssessment(AssessmentCompleted event) {
        assessmentSink.tryEmitNext(event);
    }

    public void publishOffer(OfferResponse event) {
        offerSink.tryEmitNext(event);
    }

    // методы для GraphQL Subscription
    public Flux<AssessmentCompleted> getAssessmentStream() {
        return assessmentSink.asFlux();
    }

    public Flux<OfferResponse> getOfferStream() {
        return offerSink.asFlux();
    }
}
