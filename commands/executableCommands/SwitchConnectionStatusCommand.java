package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.NOT_NORMAL;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.SUCCESS_STATUS;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class SwitchConnectionStatusCommand implements Executable {

  /**
   * The method first checks if the user with the given username is a normal user (not an artist or
   * host). If not, it returns an error message. If the user does not exist, it returns an error
   * message. If the user is online, it updates the audio trackbar. If the user is offline, it sets
   * the last command timestamp. Finally, it toggles the online status of the user and returns a
   * success message.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A BaseOutput object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    String username = command.getUsername();
    UserInput user = LibraryInput.getInstance().getUserByName(username);

    boolean isNormalUser = true;

    for (ArtistInput artist : LibraryInput.getInstance().getArtists()) {
      if (artist.getUsername().equals(username)) {
        isNormalUser = false;
        break;
      }
    }

    for (HostInput host : LibraryInput.getInstance().getHosts()) {
      if (host.getUsername().equals(username)) {
        isNormalUser = false;
        break;
      }
    }

    if (!isNormalUser) {
      message = username + NOT_NORMAL.getName();
      return new Output(command, message);
    }

    if (user == null) {
      message = USERNAME + username + NO_EXIST.getName();
      return new Output(command, message);
    }

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
    }

    user.setOnline(!user.isOnline());
    message = username + SUCCESS_STATUS.getName();
    return new Output(command, message);
  }
}
