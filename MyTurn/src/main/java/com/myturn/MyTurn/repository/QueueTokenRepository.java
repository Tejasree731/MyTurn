package com.myturn.MyTurn.repository;

import com.myturn.MyTurn.model.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    List<QueueToken> findByQueueIdAndStatusOrderByTokenNumber(
            Long queueId, String status);
    QueueToken findByQueueIdAndUsernameAndStatus(
        Long queueId, String username, String status);

    @Query("SELECT MAX(t.tokenNumber) FROM QueueToken t WHERE t.queue.id = :queueId")
    Integer findMaxToken(@Param("queueId") Long queueId);


}
