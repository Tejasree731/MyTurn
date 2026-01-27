package com.myturn.MyTurn.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class QueueSocketController {

    //built in
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendUpdate(Long queueId, int currentToken) {

        messagingTemplate.convertAndSend(
            "/topic/queue/" + queueId,
            "Now Serving: " + currentToken
        );
    }
}
