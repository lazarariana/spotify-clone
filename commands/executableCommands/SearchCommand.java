package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.searchBar.SearchBar;
import commands.constants.Constants;
import commands.constants.SearchBarEnums;
import commands.constants.StatusEnums;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.ArrayList;

import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.PlayerHelperFunctions.saveTotalTimestampPodcast;
import static commands.player.TimestampTrack.updateAudioTrackbar;
import static commands.player.TimestampTrack.updatePodcastTrackbar;

public final class SearchCommand implements Executable {
  /**
   * The method first retrieves the user and search bar associated with the command. It then
   * performs a search based on the type of the command and sets the search result in the search
   * bar. If the player has a loaded podcast, it updates the podcast trackbar and saves the total
   * timestamp of the podcast. It then unloads any loaded podcast, playlist, or song in the player
   * and sets the loading status to NOT_LOADED.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the result of the command execution.
   * @throws IllegalArgumentException If the type of the command is not song, podcast, or playlist.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    String message;

    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());

    Player player = user.getPlayer();
    if (user.isOnline()) {
      updateAudioTrackbar(command, player, command.getUsername());
    } else {
      player.setLastCommandTimestamp(command.getTimestamp());
      message = user.getUsername() + IS_OFFLINE.getName();
      return new Output(command, message, new ArrayList<>());
    }

    if (user.isOnline()) {
      SearchBar searchBar = null;
      if (user != null) {
        searchBar = user.getSearchBar();
      } else {
        return null;
      }

      Output result =
          switch (command.getType().toLowerCase()) {
            case Constants.SONG -> searchBar
                .getSearchType()
                .searchMelody(LibraryInput.getInstance().getSongs(), command);

            case Constants.PODCAST -> searchBar
                .getSearchType()
                .searchPodcast(LibraryInput.getInstance().getPodcasts(), command);

            case Constants.PLAYLIST -> searchBar.getSearchType().searchPlaylist(command);

            case Constants.ALBUM -> searchBar.getSearchType().searchAlbum(command);

            case Constants.ARTIST -> searchBar.getSearchType().searchArtists(command);
            case Constants.HOST -> searchBar.getSearchType().searchHosts(command);

            default -> throw new IllegalArgumentException(SearchBarEnums.INVALID_SEARCH);
          };

      if (result != null) {
        result.setType(command.getType());
        searchBar.setSearchResult(result);
      }

      if (user.getPlayer().getLoadedPodcast() != null) {
        updatePodcastTrackbar(command, user.getPlayer(), user.getUsername());
        saveTotalTimestampPodcast(user.getPlayer());
      }

      user.getPlayer().setLoadedPodcast(null);
      user.getPlayer().setLoadedPlaylist(null);
      user.getPlayer().setLoadedSong(null);
      user.getPlayer().setLoadingStatus(StatusEnums.LoadEnum.NOT_LOADED.ordinal());

      return result;
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
