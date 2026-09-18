package staffs.leaverequestapp.common.events;

public interface RemoteEvent extends Event {
    String exchange();
    String routingKey();
}
