package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import pages.PrintCurrentPageVisitor;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;

public final class PrintCurrentPageCommand implements Executable {

  /**
   * The method first checks if the user with the given username is online. If not, it returns an
   * error message. If the user is online, it creates a PrintCurrentPageVisitor and uses it to visit
   * the user's current page. It then returns an Output object containing the result of the visit.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A BaseOutput object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());

    if (!user.isOnline()) {
      String message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    PrintCurrentPageVisitor printCurrentPageVisitor = new PrintCurrentPageVisitor();
    String message = user.getPage().accept(printCurrentPageVisitor);

    return new Output(command, message);
  }
}
