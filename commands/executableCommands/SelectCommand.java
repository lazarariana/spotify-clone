package commands.executableCommands;

import commands.constants.Constants;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import commands.searchBar.SearchBar;
import commands.constants.SearchBarEnums;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.Constants.ARTIST;
import static commands.constants.PageConstants.PAGE;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class SelectCommand implements Executable {
  /**
   * It then checks if a search result exists in the search bar. If no search result exists, it sets
   * the message to NO_PREV_SEARCH. If a search result exists but its size is less than the item
   * number in the command, it sets the message to TOO_HIGH_ID. Otherwise, it retrieves the name of
   * the selected result, sets the message to SUCCESS_SELECT with the name, and sets the selected
   * result in the search bar. Finally, it returns an Output object containing the command, the
   * message, the type of the selected result, and the name of the selected result.
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
      SearchBar searchBar = user.getSearchBar();
      String name;
      Output selectOutput;

      searchBar.setSelectedResult(null);
      if (searchBar.getSearchResult() == null) {
        message = SearchBarEnums.SelectMessagesEnum.NO_PREV_SEARCH.getName();
        selectOutput = new Output(command, message);
      } else if (searchBar.getSearchResult().getResults().size() < command.getItemNumber()) {
        message = SearchBarEnums.SelectMessagesEnum.TOO_HIGH_ID.getName();
        selectOutput = new Output(command, message);
      } else {
        name = searchBar.getSearchResult().getResults().get(command.getItemNumber() - 1);
        if (searchBar.getSearchResult().getType().equals(ARTIST)) {
          message = SearchBarEnums.SelectMessagesEnum.SUCCESS_SELECT.getName() + name + PAGE;
          ArtistInput artist = LibraryInput.getInstance().getArtistByName(name);
          user.setPage(artist.getPage());
        } else if (searchBar.getSearchResult().getType().equals(Constants.HOST)) {
          message = SearchBarEnums.SelectMessagesEnum.SUCCESS_SELECT.getName() + name + PAGE;
          HostInput host = LibraryInput.getInstance().getHostByName(name);
          user.setPage(host.getPage());
        } else {
          message =
              SearchBarEnums.SelectMessagesEnum.SUCCESS_SELECT.getName() + name + Constants.DOT;
        }

        selectOutput = new Output(command, message);
        selectOutput.setType(searchBar.getSearchResult().getType());
        selectOutput.setName(name);

        user.getSearchBar().setSelectedResult(selectOutput);
      }

      return selectOutput;
    }

    message = command.getUsername() + IS_OFFLINE.getName();
    return new Output(command, message);
  }
}
