package commands.executableCommands;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

import commands.constants.Constants;
import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

public final class RepeatCommand implements Executable {

  /**
   * If a playlist is loaded, it changes the repeat status and sets the message to
   * CHANGE_REPEAT_MODE with the new repeat mode name. If a playlist is not loaded, it changes the
   * repeat status and sets the message to CHANGE_REPEAT_MODE with the new repeat mode name. If the
   * player's loading status is not IS_LOADED, it sets the message to LOAD_ERROR_REPEAT. Finally, it
   * returns an Output object containing the command and the message.
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
        if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
          if (player.getLoadedPlaylist() != null || player.getLoadedAlbum() != null) {
            int newStatus = player.getRepeatStatus() + 1;
            player.setRepeatStatus(newStatus % StatusEnums.RepeatPlaylistEnum.REPEAT_MODES_LENGTH);

            StatusEnums.RepeatPlaylistEnum repeatIndex =
                StatusEnums.RepeatPlaylistEnum.values()[player.getRepeatStatus()];
            String repeatModeName = repeatIndex.getName().toLowerCase();

            message =
                PlayerEnums.RepeatMessagesEnum.CHANGE_REPEAT_MODE.getName()
                    + repeatModeName
                    + Constants.DOT;
          } else {
            int newStatus = player.getRepeatStatus() + 1;
            player.setRepeatStatus(
                newStatus % StatusEnums.RepeatAudioCollectionEnum.REPEAT_MODES_LENGTH);

            String repeatModeName =
                StatusEnums.RepeatAudioCollectionEnum.values()[player.getRepeatStatus()]
                    .getName()
                    .toLowerCase();

            message =
                PlayerEnums.RepeatMessagesEnum.CHANGE_REPEAT_MODE.getName()
                    + repeatModeName
                    + Constants.DOT;
          }
        } else {
          message = PlayerEnums.RepeatMessagesEnum.LOAD_ERROR_REPEAT.getName();
        }
        return new Output(command, message);
      } else {
        message = PlayerEnums.RepeatMessagesEnum.LOAD_ERROR_REPEAT.getName();
        return new Output(command, message);
      }
    }
    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
