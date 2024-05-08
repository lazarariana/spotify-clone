package commands.executableCommands;

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

import java.util.List;

import static commands.constants.Constants.DOT;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;
import static commands.player.TimestampTrack.getSongRemainingTime;
import static commands.player.TimestampTrack.getPodcastRemainingTime;
import static commands.player.TimestampTrack.getShufflePlaylistRemainingTime;

public final class PrevCommand implements Executable {

  /**
   * If a song is loaded, it adjusts the total timestamp and sets the track name to the song's name.
   * If a playlist is loaded, it determines the current song, calculates the elapsed time, and
   * adjusts the total timestamp and track name based on the repeat status. If a podcast is loaded,
   * it sets the message to the result of the podcastPrev method. If the player's loading status is
   * not IS_LOADED, it sets the message to LOAD_ERROR_PREV. Player's play/pause status is set to
   * PLAY and an Output object containing the command and the message is returned.
   *
   * @param command The command to be executed which specifies the user and current timestamp in
   *     order to identify the current audio file playing.
   * @return An Output object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message = null;
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    String trackName;
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
          SongInput loadedSong = player.getLoadedSong();
          int remainingTime = getSongRemainingTime(player);

          player.setTotalTimestamp(
              player.getTotalTimestamp() - loadedSong.getDuration() - remainingTime);
        } else if (player.getLoadedPlaylist() != null) {
          PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
          Integer totalTimestamp = player.getTotalTimestamp();
          List<Integer> currentArray = player.getCurrentShuffleArray();

          SongInput currentSong =
              loadedPlaylist.getShuffleCurrentPlayingSong(totalTimestamp, currentArray);
          assert currentSong != null;
          int elapsedTime = currentSong.getDuration() - getShufflePlaylistRemainingTime(player);

          if (player.getRepeatStatus() == StatusEnums.RepeatPlaylistEnum.NO_REPEAT.ordinal()) {
            trackName = noRepeatPlaylist(player, currentSong, elapsedTime);
          } else if (player.getRepeatStatus()
              == StatusEnums.RepeatPlaylistEnum.REPEAT_CURRENT_SONG.ordinal()) {
            trackName = repeatCurrentSongPlaylist(elapsedTime, player);
          } else {
            trackName = repeatAllPlaylist(elapsedTime, player);
          }
          message = PlayerEnums.PrevMessagesEnum.SUCCESS_PREV.getName() + trackName + DOT;
        } else {
          message = podcastPrev(player);
        }
      } else {
        message = PlayerEnums.PrevMessagesEnum.LOAD_ERROR_PREV.getName();
        return new Output(command, message);
      }

      player.setPlayPauseStatus(StatusEnums.PlayPauseEnum.PLAY.ordinal());

      return new Output(command, message);
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }

  /**
   * The method first determines the current episode and calculates the elapsed time. If the current
   * episode is the first episode in the podcast, it will be started again from 0 and the track name
   * is set toto the current episode's name. Otherwise, if less than 1 has passed from current
   * episode, it calculates the previous total timestamp, determines the previous song, and sets the
   * track name to the previous song's name. If the elapsed time is not less than 1, it adjusts the
   * total timestamp and sets the track name to the current episode's name. Finally, it constructs a
   * message indicating the result of the operation and returns it.
   *
   * @param player The player that is playing the podcast.
   * @return A string message indicating the result of the operation.
   */
  private static String podcastPrev(final Player player) {
    String trackName;
    String message;
    PodcastInput loadedPodcast = player.getLoadedPodcast();
    EpisodeInput firstEpisode = loadedPodcast.getEpisodes().get(0);
    Integer totalTimestamp = player.getTotalTimestamp();
    int remainingTime = getPodcastRemainingTime(player);
    EpisodeInput currentEpisode = loadedPodcast.getCurrentPlayingEpisode(totalTimestamp);

    assert currentEpisode != null;
    Integer elapsedTime = currentEpisode.getDuration() - remainingTime;

    if (firstEpisode.getName().equals(currentEpisode.getName())) {
      trackName = currentEpisode.getName();
      player.setTotalTimestamp(0);
    } else {
      if (elapsedTime < 1) {
        Integer prevTotalTimestamp = totalTimestamp - elapsedTime - 1;
        EpisodeInput prevEpisode = loadedPodcast.getCurrentPlayingEpisode(prevTotalTimestamp);

        assert prevEpisode != null;
        trackName = prevEpisode.getName();
      } else {
        trackName = currentEpisode.getName();
        player.setTotalTimestamp(totalTimestamp - elapsedTime);
      }
    }
    message = PlayerEnums.PrevMessagesEnum.SUCCESS_PREV.getName() + trackName + DOT;

    return message;
  }

  /**
   * The method first checks if the elapsed time is less than 1. If it is, it adjusts the total
   * timestamp, determines the previous song, sets the total timestamp to the total duration of
   * played songs, and sets the track name to the previous song's name. If the elapsed time is not
   * less than 1, it adjusts the total timestamp, determines the previous song, sets the total
   * timestamp to the total duration of played songs, and sets the track name to the previous song's
   * name.
   *
   * @param elapsedTime The elapsed time since the last track change.
   * @param player The player that is playing the playlist.
   * @return A string representing the name of the track.
   */
  private static String repeatAllPlaylist(final int elapsedTime, final Player player) {
    String trackName;
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    List<Integer> currentArray = player.getCurrentShuffleArray();

    if (elapsedTime < 1) {
      player.setTotalTimestamp(player.getTotalTimestamp() - 1);
      SongInput prevSong =
          loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
      assert prevSong != null;
      trackName = prevSong.getName();
      player.setTotalTimestamp(
          loadedPlaylist.getShufflePlayedSongsTotalDuration(
              player.getTotalTimestamp(), currentArray));
    } else {

      player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);
      SongInput prevSong =
          loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
      player.setTotalTimestamp(
          loadedPlaylist.getShufflePlayedSongsTotalDuration(
              player.getTotalTimestamp(), currentArray));
      assert prevSong != null;
      trackName = prevSong.getName();
    }
    return trackName;
  }

  /**
   * The method first checks if the elapsed time is less than 1. If it is, it adjusts the total
   * timestamp, determines the previous song, sets the total timestamp to the total duration of
   * played songs, and sets the track name to the previous song's name. If the elapsed time is not
   * less than 1, it adjusts the total timestamp, determines the previous song, sets the total
   * timestamp to the total duration of played songs, and sets the track name to the previous song's
   * name. Finally, it returns the track name.
   *
   * @param elapsedTime The elapsed time since the last track change.
   * @param player The player that is playing the playlist.
   * @return A string representing the name of the track.
   */
  private static String repeatCurrentSongPlaylist(final int elapsedTime, final Player player) {
    String trackName;
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    List<Integer> currentArray = player.getCurrentShuffleArray();

    if (elapsedTime < 1) {
      player.setTotalTimestamp(player.getTotalTimestamp() - 1);
      SongInput prevSong =
          loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
      assert prevSong != null;
      trackName = prevSong.getName();
      player.setTotalTimestamp(
          loadedPlaylist.getShufflePlayedSongsTotalDuration(
              player.getTotalTimestamp(), currentArray));
    } else {
      player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);
      SongInput prevSong =
          loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
      player.setTotalTimestamp(
          loadedPlaylist.getShufflePlayedSongsTotalDuration(
              player.getTotalTimestamp(), currentArray));

      assert prevSong != null;
      trackName = prevSong.getName();
    }
    return trackName;
  }

  /**
   * The method first checks if the current song is the first song in the playlist. If it is,it
   * adjusts the total timestamp and sets the track name to the current song's name. Otherwise, it
   * checks if the elapsed time is less than 1. If it is, it adjusts the total timestamp, determines
   * the previous song, sets the total timestamp to the total duration of played songs, and sets the
   * track name to the previous song's name. If the elapsed time is not less than 1, it adjusts the
   * total timestamp, determines the previous song, sets the total timestamp to the total duration
   * of played songs, and sets the track name to the previous song's name.
   *
   * @param player The player that is playing the playlist.
   * @param currentSong The current song being played in the playlist.
   * @param elapsedTime The elapsed time since the last track change.
   * @return A string representing the name of the track.
   */
  private static String noRepeatPlaylist(
      final Player player, final SongInput currentSong, final int elapsedTime) {
    String trackName;
    PlaylistInput loadedPlaylist = player.getLoadedPlaylist();
    List<Integer> currentArray = player.getCurrentShuffleArray();
    SongInput firstShuffledSong =
        loadedPlaylist.getSongs().get(player.getCurrentShuffleArray().get(0));

    if (firstShuffledSong.getName().equals(currentSong.getName())) {
      trackName = currentSong.getName();
      player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);
    } else {
      if (elapsedTime < 1) {
        player.setTotalTimestamp(player.getTotalTimestamp() - 1);

        SongInput prevSong =
            loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
        assert prevSong != null;
        trackName = prevSong.getName();

        Integer totalDuration =
            loadedPlaylist.getShufflePlayedSongsTotalDuration(
                player.getTotalTimestamp(), currentArray);

        player.setTotalTimestamp(totalDuration);
      } else {
        player.setTotalTimestamp(player.getTotalTimestamp() - elapsedTime);

        SongInput prevSong =
            loadedPlaylist.getShuffleCurrentPlayingSong(player.getTotalTimestamp(), currentArray);
        assert prevSong != null;
        trackName = prevSong.getName();

        Integer totalDuration =
            loadedPlaylist.getShufflePlayedSongsTotalDuration(
                player.getTotalTimestamp(), currentArray);
        player.setTotalTimestamp(totalDuration);
      }
    }

    return trackName;
  }
}
