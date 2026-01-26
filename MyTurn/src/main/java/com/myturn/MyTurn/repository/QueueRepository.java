package com.myturn.MyTurn.repository;

import com.myturn.MyTurn.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue, Long> {
}
