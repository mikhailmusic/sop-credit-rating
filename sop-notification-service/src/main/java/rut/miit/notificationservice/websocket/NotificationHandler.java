package rut.miit.notificationservice.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);
    private final Set<WebSocketSession> sessions;
    private final ConcurrentHashMap<WebSocketSession, Set<String>> subscriptionsBySession;
    private final ObjectMapper objectMapper;

    public NotificationHandler(ObjectMapper objectMapper) {
        this.sessions = ConcurrentHashMap.newKeySet();
        this.subscriptionsBySession = new ConcurrentHashMap<>();
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        subscriptionsBySession.put(session, ConcurrentHashMap.newKeySet());

        log.info("[WS] New WebSocket connection established: sessionId={}", session.getId());
        log.info("[WS] Total active connections: {}", sessions.size());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();

        JsonNode node;
        try {
            node = objectMapper.readTree(payload);
        } catch (Exception ex) {
            log.debug("[WS] Non-JSON message ignored: {}", payload);
            return;
        }

        // PING -> PONG with sentAt preserved
        if (node.has("type") && "PING".equalsIgnoreCase(node.get("type").asText())) {
            long sentAt = node.has("sentAt") ? node.get("sentAt").asLong() : 0;
            String pongMessage = String.format("{\"type\":\"PONG\",\"sentAt\":%d}", sentAt);
            sendMessage(session, new TextMessage(pongMessage));
            log.debug("[WS] Responded with PONG to session {}", session.getId());
            return;
        }

        // SUBSCRIBE: {"cmd":"SUBSCRIBE","types":["OFFER_GENERATED","ASSESSMENT_COMPLETED"]}
        if (node.has("cmd") && "SUBSCRIBE".equalsIgnoreCase(node.get("cmd").asText())) {
            Set<String> set = subscriptionsBySession.computeIfAbsent(session, s -> ConcurrentHashMap.newKeySet());
            set.clear();

            if (node.has("types") && node.get("types").isArray()) {
                for (JsonNode t : node.get("types")) {
                    if (t != null && !t.asText().isBlank()) {
                        set.add(t.asText());
                    }
                }
            }

            log.info("[WS] Subscriptions updated: sessionId={}, types={}", session.getId(), set);

            sendMessage(session, new TextMessage("{\"type\":\"SUBSCRIBED\",\"types\":" +
                    objectMapper.writeValueAsString(set) + "}"));
            return;
        }

        log.warn("[WS] Ignored message from {}: {}", session.getId(), payload);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        subscriptionsBySession.remove(session);

        log.info("[WS] WebSocket connection closed: sessionId={} status={}", session.getId(), status);
        log.info("[WS] Total active connections: {}", sessions.size());
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        log.warn("[WS] Transport error: sessionId={}, err={}", session.getId(), exception.getMessage());
        sessions.remove(session);
        subscriptionsBySession.remove(session);
    }

    public void broadcast(String eventType, String jsonMessage) {
        int success = 0;
        int fail = 0;

        TextMessage textMessage = new TextMessage(jsonMessage);

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                subscriptionsBySession.remove(session);
                continue;
            }

            Set<String> subs = subscriptionsBySession.get(session);
            if (subs == null || subs.isEmpty()) {
                log.debug("[WS] Session {} has no subscriptions -> skip {}", session.getId(), eventType);
                continue;
            }

            if (!subs.contains(eventType)) {
                log.debug("[WS] Session {} filtered out event type {}", session.getId(), eventType);
                continue;
            }

            if (sendMessage(session, textMessage)) {
                success++;
            } else {
                fail++;
            }
        }

        log.info("[WS] Broadcast type={} success={} fail={} filtered={}",
                eventType, success, fail, sessions.size() - success - fail);
    }

    private boolean sendMessage(WebSocketSession session, TextMessage message) {
        if (!session.isOpen()) {
            sessions.remove(session);
            subscriptionsBySession.remove(session);
            return false;
        }
        try {
            synchronized (session) {
                session.sendMessage(message);
            }
            return true;
        } catch (IOException e) {
            log.error("[WS] Error sending message to session {}", session.getId(), e);
            sessions.remove(session);
            subscriptionsBySession.remove(session);
            return false;
        }
    }
}