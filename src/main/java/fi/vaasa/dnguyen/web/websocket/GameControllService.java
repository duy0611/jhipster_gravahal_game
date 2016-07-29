package fi.vaasa.dnguyen.web.websocket;

import java.security.Principal;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import fi.vaasa.dnguyen.web.websocket.dto.GameActivityDTO;

@Controller
public class GameControllService {

	private static final Logger log = LoggerFactory.getLogger(GameControllService.class);
	
	@Inject
    SimpMessageSendingOperations messagingTemplate;
	
	@SubscribeMapping("/gravahal/gameactivity")
    @SendTo("/gravahal/gamestate")
    public GameActivityDTO sendActivity(@Payload GameActivityDTO gameActivityDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) {
        gameActivityDTO.setUserLogin(principal.getName());
        
        log.debug("Sending user game's activity data {}", gameActivityDTO);
        
        return gameActivityDTO;
    }
	
}
