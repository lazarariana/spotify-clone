package commands.jsonReader;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class GetTop5Output implements BaseOutput {
  private String command;
  private String user;
  private Integer timestamp;
  private List<String> result;

  public GetTop5Output(final Command command, final List<String> result) {
    this.command = command.getCommand();
    this.user = command.getUsername();
    this.timestamp = command.getTimestamp();
    this.result = result;
  }
}
