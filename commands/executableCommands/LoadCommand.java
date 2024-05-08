package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.constants.Constants;
import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;
import fileio.input.AlbumInput;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import fileio.input.PodcastInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.PlayerHelperFunctions.buildNoShuffleIndexArray;
import static commands.player.PlayerHelperFunctions.verifyStartedPodcast;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class LoadCommand implements Executable {
  /**
   * If a result is selected, it initializes the load operation and checks the type of the selected
   * result. It then clears the search result and the selected result in the search bar. If no
   * result is selected, it sets the message to SELECT_ERROR_MESSAGE. Finally, it returns a new
   * Output object with the command and the message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return A BaseOutput object representing the result of the command execution.
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
      if (user.getSearchBar().getSelectedResult() != null) {

        initLoad(command, player);

        if (user.getSearchBar().getSelectedResult().getType().equals(Constants.PODCAST)) {
          message = loadPodcast(user, player);

        } else if (user.getSearchBar().getSelectedResult().getType().equals(Constants.PLAYLIST)) {
          message = loadPlaylist(user, player);
        } else if (user.getSearchBar().getSelectedResult().getType().equals(Constants.ALBUM)) {
          message = loadAlbum(user, player);
        } else {
          message = loadSong(user, player);
        }

        user.getSearchBar().setSearchResult(null);
        user.getSearchBar().setSelectedResult(null);
      } else {
        message = PlayerEnums.LoadMessagesEnum.SELECT_ERROR_MESSAGE.getName();
      }

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * The method retrieves the selected song. If the song is found, it sets the song as the loaded
   * song in the player and sets the message to LOAD_MESSAGE. Finally, it returns the message.
   *
   * @param user The UserInput object.
   * @param player The Player object.
   * @return A String representing the result of the operation.
   */
  private static String loadSong(final UserInput user, final Player player) {
    String message = null;
    SongInput song =
            LibraryInput.getInstance().getSongByName(user.getSearchBar()
                    .getSelectedResult().getName());

    if (song != null) {
      player.setLoadedSong(song);
      message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();

      user.getListens().add(song);
      LibraryInput.getInstance().getArtistByName(song.getArtist()).getFans().add(user);

      if (user.isPremium()) {
        user.getPremiumSongs().add(song);
      } else {
        user.getFreeSongs().add(song);
      }

      player.setWaitingForAd(false);
      player.setStartedAd(false);
    }

    return message;
  }

  /**
   * The method retrieves the selected playlist and sets it as the loaded playlist in the player. If
   * the playlist is found, it sets the message to LOAD_MESSAGE, the shuffle status to NOT_SHUFFLED,
   * and the shuffle array to the no shuffle index array. If the playlist is not found, it sets the
   * message to EMPTY_AUDIO_MESSAGE.
   *
   * @param user The UserInput object.
   * @param player The Player object.
   * @return A String representing the result of the operation.
   */
  private static String loadPlaylist(final UserInput user, final Player player) {
    String message;
    PlaylistInput selectedPlaylist =
            PlaylistInput.getPlaylistByName(user.getSearchBar().getSelectedResult().getName());
    player.setLoadedPlaylist(selectedPlaylist);

    if (selectedPlaylist != null) {
      message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();
      player.setShuffleStatus(StatusEnums.ShuffleEnum.NOT_SHUFFLED.ordinal());
      player.setCurrentShuffleArray(buildNoShuffleIndexArray(selectedPlaylist));

      SongInput song = selectedPlaylist.getSongs().get(0);
      if (song != null) {
        user.getListens().add(song);
        LibraryInput.getInstance().getArtistByName(song.getArtist()).getFans().add(user);

        if (user.isPremium()) {
          user.getPremiumSongs().add(song);
        } else {
          user.getFreeSongs().add(song);
        }
      }

      player.setWaitingForAd(false);
      player.setStartedAd(false);
    } else {
      message = PlayerEnums.LoadMessagesEnum.EMPTY_AUDIO_MESSAGE.getName();
    }

    return message;
  }

  /**
   * The method retrieves the selected podcast. If the podcast is found and the podcast has not
   * started, it sets the podcast as the loaded podcast in the player and sets the message to
   * LOAD_MESSAGE. If the podcast is not found, it sets the message to EMPTY_AUDIO_MESSAGE.
   *
   * @param user The UserInput object.
   * @param player The Player object.
   * @return A String representing the result of the operation.
   */
  private static String loadPodcast(final UserInput user, final Player player) {
    String message;
    PodcastInput selectedPodcast =
            PodcastInput.getPodcastByName(user.getSearchBar().getSelectedResult().getName());
    if (selectedPodcast != null) {
      if (Boolean.FALSE.equals(verifyStartedPodcast(player, selectedPodcast))) {
        player.setLoadedPodcast(selectedPodcast);
      }
      message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();
    } else {
      message = PlayerEnums.LoadMessagesEnum.EMPTY_AUDIO_MESSAGE.getName();
    }

    EpisodeInput episode = selectedPodcast.getEpisodes().get(0);

    if (episode != null) {
      user.getListens().add(episode);
    }

    return message;
  }

  /**
   * The method sets the loading status to IS_LOADED, the play/pause status to PLAY, and the repeat
   * status to NO_REPEAT. It also sets the total timestamp to 0, the current shuffle index to 0, and
   * the last command timestamp to the timestamp of the command.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @param player The Player object.
   */
  static void initLoad(final Command command, final Player player) {
    player.setLoadingStatus(StatusEnums.LoadEnum.IS_LOADED.ordinal());
    player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PLAY.ordinal());
    player.setRepeatStatus(StatusEnums.RepeatAudioCollectionEnum.NO_REPEAT.ordinal());

    player.setTotalTimestamp(0);
    player.setCurrentShuffleIndex(0);
    player.setLastCommandTimestamp(command.getTimestamp());
  }

  private static String loadAlbum(final UserInput user, final Player player) {
    String message;
    AlbumInput selectedAlbum =
            AlbumInput.getAlbumByName(user.getSearchBar().getSelectedResult().getName());
    player.setLoadedPlaylist(selectedAlbum);

    if (selectedAlbum != null) {
      message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();
      player.setShuffleStatus(StatusEnums.ShuffleEnum.NOT_SHUFFLED.ordinal());
      player.setCurrentShuffleArray(buildNoShuffleIndexArray(selectedAlbum));

      SongInput song = selectedAlbum.getSongs().get(0);

      if (song != null) {
        user.getListens().add(song);
        LibraryInput.getInstance().getArtistByName(song.getArtist()).getFans().add(user);

        if (user.isPremium()) {
          user.getPremiumSongs().add(song);
        } else {
          user.getFreeSongs().add(song);
        }
      }

      player.setWaitingForAd(false);
      player.setStartedAd(false);
    } else {
      message = PlayerEnums.LoadMessagesEnum.EMPTY_AUDIO_MESSAGE.getName();
    }

    return message;
  }
}
