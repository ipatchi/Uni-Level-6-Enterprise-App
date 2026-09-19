package staffs.leaverequestapp.common.events;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@AllArgsConstructor
public class RemoteOutboxListener { // Only invoked when a remote event is published
    private final EventStoreService eventStoreService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitOutboxRouter rabbitOutboxRouter;

    @Async // separate thread to HTTP request
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(
            includes = { AmqpException.class },
            maxRetries = 2,
            delay = 500,
            multiplier = 2.0
    )// runs after successful DB commit
    public void handleRemoteEvent(RemoteEvent event) {
        RabbitOutboxRouter.Destination destination;

        try { // Ensure destination exists - if not, no point trying to send!
            destination = rabbitOutboxRouter.resolve(event);
        } catch (IllegalArgumentException e) {
            log.error("Unroutable event [{}]. Check RabbitOutboxRouter configuration",
                    event.getClass().getSimpleName(),
                    e);
            eventStoreService.updateStatus(event.id(),
                    EventStoreService.StatusOfMessageDelivery.UNROUTABLE,
                    false);
            return;
        }

        rabbitTemplate.convertAndSend(destination.exchange(),
                destination.routingKey(),
                event);

        // if successful – mark as PUBLISHED
        eventStoreService.updateStatus(event.id(),
                EventStoreService.StatusOfMessageDelivery.PUBLISHED,
                false);

    }

    @Recover
    public void recover(AmqpException e, RemoteEvent event) {
        log.error("Failed to publish {} to RabbitMQ after retries. Assigning to Outbox poller",
                event.id(), e);
        eventStoreService.updateStatus(event.id(),
                EventStoreService.StatusOfMessageDelivery.FAILED,
                true);
    }
}