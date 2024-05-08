package commands.executableCommands;

import commands.constants.Constants;
import commands.constants.PlaylistEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.playlists.PlaylistHelperFunctions;
import commands.constants.StatusEnums;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class SwitchVisibilityCommand implements Executable {

  /**
   * The method first retrieves the user and playlist associated with the command. If the playlist
   * does not exist, it sets the message to indicate that the specified playlist ID is too high. If
   * the playlist exists, it checks the visibility of the playlist. If the visibility is PRIVATE, it
   * changes the visibility to PUBLIC. If the visibility is not PRIVATE, it changes the visibility
   * to PRIVATE. It then sets the message to indicate that the visibility status was updated
   * successfully. Finally, it returns an Output object containing the command and the message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the command and the result message.
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
    }

    if (user.isOnline()) {
      PlaylistInput playlist = PlaylistHelperFunctions.getPlaylistById(user, command);

      if (playlist == null) {
        message = PlaylistEnums.SwitchVisibilityEnum.TOO_HIGH_ID.getName();
      } else {
        if (playlist.getVisibility().equals(StatusEnums.VisibilityEnum.PRIVATE.getName())) {
          playlist.setVisibility(StatusEnums.VisibilityEnum.PUBLIC.getName());
        } else {
          playlist.setVisibility(StatusEnums.VisibilityEnum.PRIVATE.getName());
        }
        message =
            PlaylistEnums.SwitchVisibilityEnum.SUCCESS_SWITCH.getName()
                + playlist.getVisibility()
                + Constants.DOT;
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
