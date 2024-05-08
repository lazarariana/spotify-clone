package commands.executableCommands;

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

public final class CreatePlaylistCommand implements Executable {

  /**
   * This method creates a new playlist for a user. It first checks if a playlist with the same name
   * already exists for the user. If it does, it returns an error message. If not, it creates a new
   * playlist, sets the date created, owner, visibility, name, and followers, and adds the playlist
   * to the user's created playlists and the library's list of playlists. It then returns an Output
   * object containing the command and a success message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return Output object containing the command and a success or error message.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    PlaylistInput playlist = new PlaylistInput(command);
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
      if (PlaylistHelperFunctions.playlistAlreadyExists(user, command)) {
        message = PlaylistEnums.CreatePlaylistEnum.ALREADY_EXISTS.getName();
      } else {
        message = PlaylistEnums.CreatePlaylistEnum.SUCCESS_CREATE.getName();
        playlist.setOwner(user.getUsername());
        playlist.setVisibility(StatusEnums.VisibilityEnum.PUBLIC.getName());
        playlist.setFollowers(0);

        user.getCreatedPlaylists().add(playlist);
        LibraryInput.getInstance().getPlaylists().add(playlist);
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
