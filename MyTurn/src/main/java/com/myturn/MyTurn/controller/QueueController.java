package com.myturn.MyTurn.controller;
import com.myturn.MyTurn.model.Queue;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myturn.MyTurn.model.QueueToken;
import com.myturn.MyTurn.repository.QueueRepository;
import com.myturn.MyTurn.repository.QueueTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/queue")
public class QueueController {
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private QueueTokenRepository queueTokenRepository;
    @PostMapping("/create")
    public String createQueue(@RequestParam String name) {
        Queue q=new Queue();
        q.setName(name);
        queueRepository.save(q);
        return "Queue created with name: "+name;
    }
    
    @PostMapping("/join/{qid}")
    public String joinQueue(@PathVariable Long qid,@RequestParam String username) {
        Queue q=queueRepository.findById(qid)
                    .orElse(null);
        if(q==null || !q.isActive()){
            return "Queue doesnot exists!";
        }

        Integer lastToken = queueTokenRepository.findMaxToken(qid);

        if (lastToken == null) lastToken = 0;

        int newToken =(lastToken)+1;
        QueueToken token=new QueueToken();
        token.setTokenNumber(newToken);
        token.setUsername(username);
        token.setQueue(q);
        queueTokenRepository.save(token);
        return "Joined Queue Successfully.\nYour Token: "+(newToken);

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
        return "Now Serving Token: "+current+1;
    }
    @GetMapping("/position/{qid}")
    public String getMethodName(@PathVariable Long qid,@RequestParam String username) {
        Queue q=queueRepository.findById(qid)
                    .orElse(null);
        if(q==null || !q.isActive()){
            return "Queue doesnot exists!";
        }

        QueueToken token =
        queueTokenRepository
            .findByQueueIdAndUsernameAndStatus(
                qid, username, "WAITING");

        if(token==null){
             return "You're not in this queue!";
        }
        int userToken=token.getTokenNumber();
        int current=q.getCurrentToken();
        int ahead=userToken-current-1;
        if(ahead<0) ahead=0;
        return "Your Token: " + userToken +
           " | Now Serving: " + current +
           " | People Ahead: " + ahead;
    }
    
    
    
}
