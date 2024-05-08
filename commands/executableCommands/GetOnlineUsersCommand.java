package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.UsersResult;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public final class GetOnlineUsersCommand implements Executable {

  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<String> onlineUsers = new ArrayList<>();
    UsersResult output = new UsersResult(command);

    for (UserInput user : LibraryInput.getInstance().getUsers()) {
      if (user.isOnline()) {
        onlineUsers.add(user.getUsername());
      }
    }
    output.setResult(onlineUsers);

    return output;
  }
}
