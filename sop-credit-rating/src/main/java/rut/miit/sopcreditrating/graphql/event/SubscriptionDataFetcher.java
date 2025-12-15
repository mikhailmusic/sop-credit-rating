package rut.miit.sopcreditrating.graphql.event;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import rut.miit.sopcontracts.dto.response.AssessmentCompleted;
import rut.miit.sopcontracts.dto.response.OfferResponse;

import java.util.UUID;

@DgsComponent
public class SubscriptionDataFetcher {
    private GraphqlEventPublisher publisher;

    @DgsSubscription
    public Publisher<AssessmentCompleted> assessmentUpdates(@InputArgument("applicationId") UUID applicationId) {
        return publisher.getAssessmentStream().filter(a -> a.applicationId().equals(applicationId));
    }

    @DgsSubscription
    public Publisher<OfferResponse> offerProposals(@InputArgument("applicationId") UUID applicationId) {
        return publisher.getOfferStream().filter(o -> o.getApplicationId().equals(applicationId));
    }

    @Autowired
    public void setPublisher(GraphqlEventPublisher publisher) {
        this.publisher = publisher;
    }
}
