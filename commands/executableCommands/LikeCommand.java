package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.constants.PlayerEnums;
import fileio.input.AlbumInput;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class LikeCommand implements Executable {

  /**
   * This method likes/unlikes a song playing. If the song has been liked, it is currently stored in
   * an arraylist and will be removed from it and sets the message to SUCCESS_LIKE. Otherwise, the
   * new song is added to the liked songs of the user specified in command and the message is set to
   * SUCCESS_LIKE. For the statistic command, number of likes for a song is decreased/increased
   * depending on the effect that like command has on the array. If the player is not loaded, it
   * sets the message to LOAD_ERROR_LIKE.
   *
   * @param command The Command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return A BaseOutput object representing the audio files which match all filters.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message = null;
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
        SongInput currentSong = null;

        if (player.getLoadedPlaylist() != null) {
          PlaylistInput loadedPlaylist = player.getLoadedPlaylist();

          currentSong =
              loadedPlaylist.getShuffleCurrentPlayingSong(
                  player.getTotalTimestamp(), player.getCurrentShuffleArray());

        } else if (player.getLoadedSong() != null) {
          currentSong = player.getLoadedSong();
        } else if (player.getLoadedAlbum() != null) {
          AlbumInput loadedAlbum = player.getLoadedAlbum();
          currentSong =
              loadedAlbum.getShuffleCurrentPlayingSong(
                  player.getTotalTimestamp(), player.getCurrentShuffleArray());
        } else {
          message = PlayerEnums.LikeMessagesEnum.FILE_ERROR.getName();
        }

        if (currentSong == null) {
          return new Output(command, message);
        }

        if (user.getLikedSongs().contains(currentSong)) {
          message = PlayerEnums.LikeMessagesEnum.SUCCESS_UNLIKE.getName();
          user.getLikedSongs().remove(currentSong);
          currentSong.setLikes(currentSong.getLikes() - 1);
        } else {
          message = PlayerEnums.LikeMessagesEnum.SUCCESS_LIKE.getName();
          user.getLikedSongs().add(currentSong);
          currentSong.setLikes(currentSong.getLikes() + 1);
        }
      } else {
        message = PlayerEnums.LikeMessagesEnum.LOAD_ERROR_LIKE.getName();
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
