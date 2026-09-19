package staffs.leaverequestapp.common.events.infra;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity(name="event_store")
@Table(name="event_store")
@ToString
@Getter
@Setter
public class EventStoreJpa{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="occurred_on")
    private Instant occurredOn;

    @Column(name="event_body")
    private String eventBody;

    @Column(name="event_type")
    private String eventType;

    @Column(name="status")
    private String status = "PENDING";

    @Column(name="retry_count")
    private int retryCount = 0;
}


