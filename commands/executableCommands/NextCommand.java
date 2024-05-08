package commands.executableCommands;

import java.util.List;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.constants.PlayerEnums;
import commands.constants.StatusEnums;

import fileio.input.EpisodeInput;
import fileio.input.PlaylistInput;
import fileio.input.PodcastInput;
import fileio.input.UserInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;

import static commands.constants.Constants.DOT;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.getSongRemainingTime;
import static commands.player.TimestampTrack.getShufflePlaylistRemainingTime;
import static commands.player.TimestampTrack.getPodcastRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class NextCommand implements Executable {
  /**
   * This method handles the logic for the next action in a media player. It takes into account the
   * current state of the player, the loaded media (song, playlist, or podcast), and the repeat
   * status. It updates the audio trackbar, checks if the media is loaded, and performs the
   * appropriate action based on the repeat status. If no media is loaded, it returns an error
   * message. If a song, playlist, or podcast is loaded, it calculates the remaining time, updates
   * the total timestamp, and sets the next track or episode. It also sets the play/pause status to
   * play. The method returns an Output object containing the command and a success or error
   * message.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return Output object containing the command and a success or error message.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;
    String trackName = null;
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
      if (player.getLoadingStatus() == StatusEnums.LoadEnum.IS_LOADED.ordinal()) {
        if (player.getLoadedSong() != null) {

          if (player.getRepeatStatus()
              == StatusEnums.RepeatAudioCollectionEnum.NO_REPEAT.ordinal()) {
            return unLoad(command, player);
          } else {
            trackName = player.getLoadedSong().getName();
            player.setTotalTimestamp(player.getTotalTimestamp() + getSongRemainingTime(player) + 1);
            message = PlayerEnums.NextMessagesEnum.SUCCESS_NEXT.getName() + trackName + DOT;
          }
        } else if (player.getLoadedPlaylist() != null) {
          PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
          Integer totalTimestamp = player.getTotalTimestamp();
          List<Integer> currentArray = player.getCurrentShuffleArray();

          SongInput currentSong =
              loadedPlaylist.getShuffleCurrentPlayingSong(totalTimestamp, currentArray);

          Integer songFinishedTimestamp =
              player.getTotalTimestamp() + getShufflePlaylistRemainingTime(player);
          Integer totalDurations = loadedPlaylist.getSummedDurationsPlaylist();
          assert currentSong != null;
          int elapsedTime = currentSong.getDuration() - getShufflePlaylistRemainingTime(player);

          if (player.getRepeatStatus() == StatusEnums.RepeatPlaylistEnum.NO_REPEAT.ordinal()) {
            if (songFinishedTimestamp >= totalDurations) {
              return unLoad(command, player);
            } else {
              trackName = noRepeatPlaylistNext(player);
            }

          } else if (player.getRepeatStatus()
              == StatusEnums.RepeatPlaylistEnum.REPEAT_ALL.ordinal()) {
            trackName = repeatAllPlaylistNext(songFinishedTimestamp, player);
          } else if (player.getRepeatStatus()
              == StatusEnums.RepeatPlaylistEnum.REPEAT_CURRENT_SONG.ordinal()) {
            trackName = repeatCurrentSongPlaylistNext(player, elapsedTime);
          }
          message = PlayerEnums.NextMessagesEnum.SUCCESS_NEXT.getName() + trackName + DOT;
        } else {
          PodcastInput loadedPodcast = player.getLoadedPodcast();
          Integer totalTimestamp = player.getTotalTimestamp();
          EpisodeInput currentEpisode =
              loadedPodcast.getCurrentPlayingEpisode(player.getTotalTimestamp());
          int episodeFinishedTimestamp = totalTimestamp + getPodcastRemainingTime(player);
          Integer totalDurations = loadedPodcast.getSummedDurationsPodcast();

          if (player.getRepeatStatus()
              == StatusEnums.RepeatAudioCollectionEnum.NO_REPEAT.ordinal()) {
            if (episodeFinishedTimestamp >= totalDurations) {
              unLoad(command, player);
            } else {
              trackName = noRepeatPodcastNext(player);
            }
          } else if (player.getRepeatStatus()
              == StatusEnums.RepeatAudioCollectionEnum.REPEAT_INFINITE.ordinal()) {
            trackName = circularLoopPodcast(episodeFinishedTimestamp, totalDurations, player);
          } else {
            assert currentEpisode != null;
            trackName = finishCurrentEpisode(currentEpisode, player);
          }
          message = PlayerEnums.NextMessagesEnum.SUCCESS_NEXT.getName() + trackName + DOT;
        }
      } else {
        message = PlayerEnums.NextMessagesEnum.LOAD_ERROR_NEXT.getName();
        return new Output(command, message);
      }

      player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PLAY.ordinal());

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * This method handles the case where there is no repeat for the podcast and gets the next
   * episode.
   *
   * @param player The Player object.
   * @return The name of the next episode.
   */
  private static String noRepeatPodcastNext(final Player player) {
    String trackName;
    player.setTotalTimestamp(player.getTotalTimestamp() + getPodcastRemainingTime(player) + 1);
    EpisodeInput nextEpisode =
        player.getLoadedPodcast().getCurrentPlayingEpisode(player.getTotalTimestamp());

    assert nextEpisode != null;
    trackName = nextEpisode.getName();

    return trackName;
  }

  /**
   * This method handles the case where the current song in the playlist is to be repeated,
   * therefore tne next song is actually itself, but the timestamp must be updated too.
   *
   * @param player The Player object.
   * @param elapsedTime The elapsed time.
   * @return The name of the next song.
   */
  private static String repeatCurrentSongPlaylistNext(final Player player, final int elapsedTime) {
    String trackName;
    player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    Integer totalTimestamp = player.getTotalTimestamp();
    List<Integer> currentArray = player.getCurrentShuffleArray();
    SongInput nextSong = loadedPlaylist.getShuffleCurrentPlayingSong(totalTimestamp, currentArray);

    assert nextSong != null;
    trackName = nextSong.getName();

    return trackName;
  }

  /**
   * This method handles the case where all songs in the playlist are to be repeated and gets the
   * next song. The last song represents an edge case, because REPEAT_ALL means that the playlist
   * has a circular structure and playlist should eb played again after finishing all songs.
   *
   * @param songFinishedTimestamp The timestamp when the song finished.
   * @param player The Player object.
   * @return The name of the next song.
   */
  private static String repeatAllPlaylistNext(
      final Integer songFinishedTimestamp, final Player player) {
    String trackName;
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    List<Integer> currentArray = player.getCurrentShuffleArray();
    int remainingTime = getShufflePlaylistRemainingTime(player);

    if (songFinishedTimestamp.equals(loadedPlaylist.getSummedDurationsPlaylist())) {
      trackName = circularLoopPlaylist(player);
    } else {
      player.setTotalTimestamp(player.getTotalTimestamp() + remainingTime);
      SongInput nextSong =
          loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
      assert nextSong != null;
      trackName = nextSong.getName();
    }

    return trackName;
  }

  /**
   * This method handles the case where there is no repeat for the playlist, therefore it gets the
   * next song in playlist.
   *
   * @param player The Player object.
   * @return The name of the next song.
   */
  private static String noRepeatPlaylistNext(final Player player) {
    String trackName;
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    List<Integer> currentArray = player.getCurrentShuffleArray();

    player.setTotalTimestamp(player.getTotalTimestamp() + getShufflePlaylistRemainingTime(player));
    SongInput nextSong =
        loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);

    assert nextSong != null;
    trackName = nextSong.getName();

    return trackName;
  }

  /**
   * This method finishes the current episode by adding to the current timestamp the remaining time
   * left.
   *
   * @param currentEpisode The current EpisodeInput object.
   * @param player The Player object.
   * @return The name of the current episode.
   */
  private static String finishCurrentEpisode(
      final EpisodeInput currentEpisode, final Player player) {
    String trackName;

    assert currentEpisode != null;
    trackName = currentEpisode.getName();
    player.setTotalTimestamp(player.getTotalTimestamp() + getPodcastRemainingTime(player) + 1);

    return trackName;
  }

  /**
   * This method handles the case where the podcast is to be looped circularly and gets the next
   * episode.
   *
   * @param episodeFinishedTimestamp The timestamp when the episode finished.
   * @param totalDurations The total durations of the episodes.
   * @param player The Player object.
   * @return The name of the next episode.
   */
  private static String circularLoopPodcast(
      final int episodeFinishedTimestamp, final Integer totalDurations, final Player player) {
    String trackName;
    PodcastInput loadedPodcast = player.getLoadedPodcast();
    Integer totalTimestamp = player.getTotalTimestamp();

    if (episodeFinishedTimestamp >= totalDurations) {
      trackName = loadedPodcast.getEpisodes().get(0).getName();
      player.setTotalTimestamp(0);
    } else {
      player.setTotalTimestamp(totalTimestamp + getPodcastRemainingTime(player) + 1);
      EpisodeInput nextEpisode = loadedPodcast.getCurrentPlayingEpisode(player.getTotalTimestamp());

      assert nextEpisode != null;
      trackName = nextEpisode.getName();
    }

    return trackName;
  }

  /**
   * This method handles the case where the playlist is to be looped circularly and gets the next
   * song.
   *
   * @param player The Player object.
   * @return The name of the next song.
   */
  private static String circularLoopPlaylist(final Player player) {
    String trackName;

    trackName = player.getLoadedPlaylist().getSongs().get(0).getName();
    player.setTotalTimestamp(0);

    return trackName;
  }

  /**
   * This method unloads the player.
   *
   * @param command The Command object.
   * @param player The Player object.
   * @return An Output object representing the result of the operation.
   */
  private static Output unLoad(final Command command, final Player player) {
    String message;
    message = PlayerEnums.NextMessagesEnum.LOAD_ERROR_NEXT.getName();

    player.setLoadingStatus(StatusEnums.LoadEnum.NOT_LOADED.ordinal());
    player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PAUSE.ordinal());
    player.setLoadedPlaylist(null);

    return new Output(command, message);
  }
}
