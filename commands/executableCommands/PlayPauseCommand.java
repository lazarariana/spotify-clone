package commands.executableCommands;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

public final class PlayPauseCommand implements Executable {

  /**
   * The method first retrieves the user and player associated with the command. It then * updates
   * the trackbar of the loaded playlist, podcast, or song in the player. If the * player's loading
   * status is IS_LOADED, it checks the play/pause status. If the play/pause * status is PAUSE, it
   * changes the status to PLAY and sets the message to PLAY_MESSAGE. If the * play/pause status is
   * not PAUSE, it changes the status to PAUSE and sets the message to * PAUSE_MESSAGE. If the
   * player's loading status is not IS_LOADED, it sets the message to * LOAD_ERROR_MESSAGE. Finally,
   * it returns an Output object containing the command and the * message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    assert user != null;

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
      message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    if (user.isOnline()) {
      if (!player.isFinished()) {

        if (player.getPlayPauseStatus() == StatusEnums.PlayPauseEnum.PAUSE.ordinal()) {
          player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PLAY.ordinal());
          message = PlayerEnums.PlayPauseMessagesEnum.PLAY_MESSAGE.getName();
        } else {
          player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PAUSE.ordinal());
          message = PlayerEnums.PlayPauseMessagesEnum.PAUSE_MESSAGE.getName();
        }
      } else {
        message = PlayerEnums.PlayPauseMessagesEnum.LOAD_ERROR_MESSAGE.getName();
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
