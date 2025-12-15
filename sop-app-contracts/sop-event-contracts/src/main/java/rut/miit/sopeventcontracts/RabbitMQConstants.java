package rut.miit.sopeventcontracts;

public final class RabbitMQConstants {

    // Exchanges
    public static final String EXCHANGE_NAME = "credit.exchange";
    public static final String FANOUT_RESULTS = "credit.results-fanout";
    public static final String DLX_EXCHANGE_NAME = "credit.dlx-exchange";

    // Routing keys
    public static final String RK_ASSESSMENT_REQ = "assessment.request";

    // Queues
    public static final String QUEUE_MAIN_RESULTS = "main-results-queue";
    public static final String QUEUE_AUDIT_REQUESTS = "audit-requests-queue";
    public static final String QUEUE_AUDIT_RESULTS = "audit-results-queue";
    public static final String QUEUE_NOTIFICATION_RESULTS = "notification-results-queue";
    public static final String QUEUE_CALC_REQUESTS = "calc-requests-queue";
    public static final String QUEUE_CALC_INTERNAL = "calc-internal-results-queue";

    // Dead letter queues
    public static final String DLQ_MAIN_RESULTS = "main-results-queue.dlq";
    public static final String DLQ_AUDIT_REQUESTS = "audit-requests-queue.dlq";
    public static final String DLQ_AUDIT_RESULTS = "audit-results-queue.dlq";
    public static final String DLQ_NOTIFICATION_RESULTS = "notification-results-queue.dlq";
    public static final String DLQ_CALC_REQUESTS = "calc-requests-queue.dlq";

    // Dead letter routing keys
    public static final String RK_DLQ_MAIN_RESULTS = "dlq.main-results";
    public static final String RK_DLQ_AUDIT_REQUESTS = "dlq.audit-requests";
    public static final String RK_DLQ_AUDIT_RESULTS = "dlq.audit-results";
    public static final String RK_DLQ_NOTIFICATION_RESULTS = "dlq.notification-results";
    public static final String RK_DLQ_CALC_REQUESTS = "dlq.calc-requests";

}