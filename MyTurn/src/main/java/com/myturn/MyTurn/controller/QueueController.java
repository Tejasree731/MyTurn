package com.myturn.MyTurn.controller;
import com.myturn.MyTurn.model.Queue;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;


import com.myturn.MyTurn.model.QueueToken;
import com.myturn.MyTurn.repository.QueueRepository;
import com.myturn.MyTurn.repository.QueueTokenRepository;
import com.myturn.MyTurn.websocket.QueueSocketController;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;




@RestController
@RequestMapping("/api/queue")
public class QueueController {
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueTokenRepository queueTokenRepository;
    @Autowired
    private QueueSocketController socketController;

    @PostMapping("/create")
    public String createQueue(@RequestParam String name) {
        Queue q=new Queue();
        q.setName(name);
        queueRepository.save(q);
        return "Queue created with name: "+name;
    }
    
   @PostMapping("/join/{qid}")
public String joinQueue(@PathVariable Long qid,
                        Authentication authentication) {

    String username = authentication.getName(); // ✅ Correct

    Queue q = queueRepository.findById(qid)
                .orElse(null);

    if(q == null || !q.isActive()){
        return "Queue does not exist!";
    }

    // Prevent duplicate entry
    QueueToken existing =
        queueTokenRepository
            .findByQueueIdAndUsernameAndStatus(
                qid, username, "WAITING");

    if(existing != null){
        return "Already joined. Your Token: " +
               existing.getTokenNumber();
    }

    Integer lastToken = queueTokenRepository.findMaxToken(qid);

    if (lastToken == null) lastToken = 0;

    int newToken = lastToken + 1;

    QueueToken token = new QueueToken();

    token.setTokenNumber(newToken);
    token.setUsername(username);
    token.setQueue(q);
    token.setStatus("WAITING");

    queueTokenRepository.save(token);

    socketController.sendUpdate(qid, q.getCurrentToken());

    return "Joined Successfully. Your Token: " + newToken;
}



    @PostMapping("/next/{qid}")
    public String callNext(@PathVariable Long qid) {
        Queue q=queueRepository.findById(qid)
                    .orElse(null);
        if(q==null || !q.isActive()){
            return "Queue doesnot exists!";
        }
        int current=q.getCurrentToken();

        //next waiting user
        QueueToken currentToken=queueTokenRepository
            .findByQueueIdAndStatusOrderByTokenNumber(qid, "WAITING")
            .stream()
            .filter(t->t.getTokenNumber()==current+1)
            .findFirst()
            .orElse(null);

        if(currentToken==null){
            return "No more users in the queue!";
        }
        currentToken.setStatus("SERVED");
        queueTokenRepository.save(currentToken);
        q.setCurrentToken(current+1);
        queueRepository.save(q);
        socketController.sendUpdate(qid, current + 1);
        return "Now Serving Token: "+(current+1);
    }
    @GetMapping("/position/{qid}")
public Map<String, Object> position(@PathVariable Long qid,
                                    Authentication authentication) {

    String username = authentication.getName();

    Queue q = queueRepository.findById(qid).orElse(null);

    if(q == null || !q.isActive()){
        throw new RuntimeException("Queue not found");
    }

    QueueToken token =
        queueTokenRepository
            .findByQueueIdAndUsernameAndStatus(
                qid, username, "WAITING");

    if(token == null){
        throw new RuntimeException("Not in queue");
    }

    int userToken = token.getTokenNumber();
    int current = q.getCurrentToken();

    int ahead = userToken - current - 1;
    if(ahead < 0) ahead = 0;

    Map<String, Object> result = new HashMap<>();

    result.put("totalPeople",
        queueTokenRepository
          .findByQueueIdAndStatusOrderByTokenNumber(qid,"WAITING")
          .size()
    );

    result.put("myPosition", ahead + 1);
    result.put("token", userToken);
    result.put("current", current);

    return result;
}

    @GetMapping("/status/{qid}")
public Map<String, Object> getStatus(@PathVariable Long qid) {

    Queue q = queueRepository.findById(qid).orElse(null);

    if (q == null) {
        throw new RuntimeException("Queue not found");
    }

    int waiting =
        queueTokenRepository
            .findByQueueIdAndStatusOrderByTokenNumber(qid, "WAITING")
            .size();

    Map<String, Object> result = new HashMap<>();

    result.put("queueId", qid);
    result.put("currentToken", q.getCurrentToken());
    result.put("waiting", waiting);
    result.put("active", q.isActive());

    return result;
}
@ExceptionHandler(Exception.class)
public String handle(Exception e){
    e.printStackTrace();
    return "ERROR: " + e.getMessage();
}

@GetMapping("/dashboard")
public List<Map<String, Object>> getDashboard() {

    List<Queue> queues = queueRepository.findAll();

    List<Map<String, Object>> result = new ArrayList<>();

    for (Queue q : queues) {

        Map<String, Object> data = new HashMap<>();

        Long qid = q.getId();

        int waitingCount =
            queueTokenRepository
                .findByQueueIdAndStatusOrderByTokenNumber(
                    qid, "WAITING")
                .size();

        data.put("queueId", qid);
        data.put("name", q.getName());
        data.put("currentToken", q.getCurrentToken());
        data.put("waiting", waitingCount);
        data.put("active", q.isActive());

        result.add(data);
    }

    return result;
}



    
}
