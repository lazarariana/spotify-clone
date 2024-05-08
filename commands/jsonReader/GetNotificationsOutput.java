package commands.jsonReader;

import lombok.Getter;
import lombok.Setter;
import notifications.Notification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetNotificationsOutput implements BaseOutput {
    private String command;
    private String user;
    private int timestamp;
    private List<Notification> notifications = new ArrayList<>();

    public GetNotificationsOutput(final Command command, final List<Notification> notifications) {
        this.command = command.getCommand();
        this.user = command.getUsername();
        this.timestamp = command.getTimestamp();
        this.notifications = notifications;
    }
}
