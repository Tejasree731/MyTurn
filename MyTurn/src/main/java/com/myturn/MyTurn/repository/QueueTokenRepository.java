package com.myturn.MyTurn.repository;

import com.myturn.MyTurn.model.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {

    List<QueueToken> findByQueueIdAndStatusOrderByTokenNumber(
            Long queueId, String status);
}
