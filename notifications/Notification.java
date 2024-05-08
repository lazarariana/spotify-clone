package notifications;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {
    private String name;
    private String description;

    public Notification(final String update) {
        int separatorIndex = update.indexOf(": ");

        if (separatorIndex != -1) {
            this.name = update.substring(0, separatorIndex);
            this.description = update.substring(separatorIndex + 2);
        } else {
            this.name = update;
            this.description = "";
        }
    }
}
