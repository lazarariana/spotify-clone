package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;

/**
 * This interface represents an executable command.
 *
 * <p>It has a single method, executeCommand, which takes a Command object as a parameter and
 * returns a BaseOutput object. The executeCommand method is meant to be implemented by any class
 * that needs to execute a command and produce an output.
 */
public interface Executable {
  /**
   * Executes a given command and returns the result of the command execution.
   *
   * @param command The command to be executed.
   * @return A BaseOutput object containing the result of the command execution.
   */
  BaseOutput executeCommand(Command command);
}
