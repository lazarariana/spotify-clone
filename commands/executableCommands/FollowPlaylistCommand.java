package commands.executableCommands;

import commands.constants.Constants;
import commands.constants.PlayerEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.UserInput;

import java.util.Iterator;

import static commands.constants.NotificationsConstants.FOLLOW_PLAYLIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USER;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class FollowPlaylistCommand implements Executable {

  /**
   * This method handles the logic for following or unfollowing a playlist. If a result is selected,
   * and it's a playlist, it checks if the user is already following the playlist. If they are, it
   * unfollows the playlist and decreases the follower count. If they're not following the playlist,
   * it checks if the playlist exists in the library and if the user is not the owner. If these
   * conditions are met, it follows the playlist and increases the follower count. If the selected
   * result is not a playlist or no result is selected, it returns an appropriate error message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return Output object containing the command and a success or error message.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message = null;
    boolean alreadyExists = false;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    Output selectedResult = null;
    assert user != null;

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, user.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
      message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message);
    }

    if (user.isOnline()) {
      if (user.getSearchBar().getSelectedResult() != null) {
        selectedResult = user.getSearchBar().getSelectedResult();
      } else {
        message = PlayerEnums.FollowEnum.NOTLOADED.getName();
      }

      if (selectedResult != null) {
        if (selectedResult.getType().equals(Constants.PLAYLIST)) {
          String playlistName = selectedResult.getName();
          Iterator<PlaylistInput> iterator = user.getFollowedPlaylists().iterator();

          while (iterator.hasNext()) {
            PlaylistInput followedPlaylist = iterator.next();

            if (followedPlaylist.getName().equals(playlistName)) {
              iterator.remove();
              followedPlaylist.setFollowers(followedPlaylist.getFollowers() - 1);
              message = PlayerEnums.FollowEnum.UNFOLLOW.getName();
              alreadyExists = true;

              followedPlaylist.unsubscribe(user);
            }
          }

          if (!alreadyExists) {
            message = follow(playlistName, user);
          }

        } else {
          message = PlayerEnums.FollowEnum.FILE_ERROR.getName();
        }
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * This method allows a user to follow a playlist in the library if it does not belong to him.
   *
   * @param playlistName The name of the playlist that the user wants to follow.
   * @param user The user who wants to follow the playlist.
   * @return A string message indicating the result of the follow action.
   */
  private static String follow(final String playlistName, final UserInput user) {
    String message = null;

    for (PlaylistInput playlist : LibraryInput.getInstance().getPlaylists()) {
      if (playlist.getName().equals(playlistName)) {
        if (!playlist.getOwner().equals(user.getUsername())) {
          user.getFollowedPlaylists().add(playlist);
          playlist.setFollowers(playlist.getFollowers() + 1);
          message = PlayerEnums.FollowEnum.FOLLOW.getName();

          playlist.subscribe(user);
          playlist.notifyObservers(USER + user.getUsername() + FOLLOW_PLAYLIST + playlistName);
        } else {
          message = PlayerEnums.FollowEnum.OWN_PLAYLIST.getName();
        }
      }
    }

    return message;
  }
}
