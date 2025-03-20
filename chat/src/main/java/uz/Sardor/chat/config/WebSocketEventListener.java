package uz.Sardor.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import uz.Sardor.chat.entity.ChatMessage;
import uz.Sardor.chat.entity.MessageType;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("username");
        if (username != null) {
            log.info("User disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVER)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
