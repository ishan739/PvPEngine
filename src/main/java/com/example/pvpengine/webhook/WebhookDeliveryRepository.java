package com.example.pvpengine.webhook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID> {

    @Query("Select w FROM WebhookDelivery w WHERE w.status = 'PENDING' AND w.nextRetryAt <= :now")
    List<WebhookDelivery> findPendingDue(OffsetDateTime now);
}
